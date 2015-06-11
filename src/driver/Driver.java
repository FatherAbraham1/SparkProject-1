package driver;

import org.apache.spark.*;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver
{
	public static void main(String[] args)
	{
		// master is a Spark, YARN cluster URL, or a special “local[*]” string to run in local mode.
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("RFID");
		int controlPort = 9000;
		int dataPort = 9001;

		try
		{
			ServerSocket controlSocketListener = new ServerSocket(controlPort);
			Socket controlSocket;
			String methodName;

			// Wait for connection on port 9000.
			System.out.println("Server listening on Port "+controlPort+ " ....");
			controlSocket = controlSocketListener.accept();

			// Get the input stream to read the function name to execute.
			System.out.println("Received Connection ....");
			DataInputStream controlReader = new DataInputStream(controlSocket.getInputStream());

			while(true)
			{
				// Read method name. Blocks till read.
				methodName = controlReader.readLine();
				System.out.println("Method Name Received: "+methodName);

				// Stream context object.
				JavaStreamingContext context = new JavaStreamingContext(conf, Durations.seconds(1));

				// Create a DStream that will connect to hostname:port, like localhost:9001
				JavaReceiverInputDStream<String> lines = context.socketTextStream("localhost", dataPort);

				// Print Line
				lines.print();
			}


		}
		catch (IOException e)
		{
			System.err.println("Could not listen on port 9000! Try flushing the ports or restarting your machine.");
		}
	}
}
