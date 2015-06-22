package driver;

import crowdedarea.CrowdedArea;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import peakhours.PeakHours;
import popularspeech.PopularSpeech;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver
{
	public static void main(String[] args)
	{
		// Delay in milliseconds between each message and the other.
		int delay = 500;

		// master is a Spark, YARN cluster URL, or a special “local[*]” string to run in local mode.
		SparkConf conf = new SparkConf().setMaster("local[3]").setAppName("RFID");
		// Stream context object. The duration is the number of output streams per unit time.
		JavaStreamingContext context = new JavaStreamingContext(conf, Durations.milliseconds(delay));

		// Create a new server socket to connect to the ruby server for the data.
		try
		{
			ServerSocket rubyConnector = new ServerSocket(9000);
			System.out.println("Server Started! Listening to port 9000 ...");

			while (true)
					{
				// Accept the connection from the client.
				Socket client = rubyConnector.accept();

				// This socket will be used to send the results of the processed spark data.
				Socket rubyOutputConnector = new Socket("localhost",9003);

				BufferedReader inputFeed = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter outputRubyFeed = new BufferedWriter(new OutputStreamWriter(rubyOutputConnector.getOutputStream()));

				String statisticName = inputFeed.readLine();
				System.out.println("Received: " + statisticName);

				try
				{
					// Create a new object using the factory.
					Calculator statistic = calculatorFactory(statisticName, outputRubyFeed);

					// Process the incoming data on the Spark data port.
					statistic.processData(context);
					System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvv$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				}
				catch (Exception e)
				{
					e.printStackTrace();

				}

				// Close the socket and all its streams.
	//			inputFeed.close();
	//			outputFeed.close();
	//			client.close();
			}
		}
		catch (IOException e)
		{
			//System.err.println(e.getMessage());
			e.printStackTrace();

		}
	}

	/**
	 * This method follows the factory design pattern to create a class to calculate each statistic based on the
	 * received method from the socket.
	 * @param method The statistic to create the object with
	 * @return An instance of the Base class of all the statistics, which is calculator.
	 */
	public static Calculator calculatorFactory(String method, BufferedWriter out) throws Exception
	{
		Calculator newObject;

		if(method.equals("peakhours"))
			newObject = new PeakHours();
		else if(method.equals("popularspeech"))
			newObject = new PopularSpeech();
		else if(method.equals("crowdedarea"))
			newObject = new CrowdedArea();
		else
			throw new Exception("Statistic not defined!");

		// Attach a reference to the socket where the data should be sent in the object.
		Calculator.outputFeed = out;

		return newObject;
	}
}
