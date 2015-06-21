package driver;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import peakhours.PeakHours;

import javax.websocket.Session;
import java.io.IOException;
import java.net.ServerSocket;

public class Driver
{
	public static void main(String[] args)
	{
		// master is a Spark, YARN cluster URL, or a special “local[*]” string to run in local mode.
		SparkConf conf = new SparkConf().setMaster("local[3]").setAppName("RFID");
		// Stream context object. The duration is the number of output streams per unit time.
		JavaStreamingContext context = new JavaStreamingContext(conf, Durations.milliseconds(3000));

		// Create a new server socket to connect to the ruby server for the data.
		try
		{
			ServerSocket rubyConnector = new ServerSocket(9000);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Calculator test = new PeakHours();
		test.processData(context);
	}

}
