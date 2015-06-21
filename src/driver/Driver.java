package driver;

import filereceiver.SparkBuffer;
import org.apache.spark.*;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.*;
import peakhours.PeakHours;

public class Driver
{
	public Driver(SparkBuffer mySparkBuffer)
	{
        // master is a Spark, YARN cluster URL, or a special “local[*]” string to run in local mode.
		SparkConf conf = new SparkConf().setMaster("local[3]").setAppName("RFID");

		// Stream context object. The duration is the number of output streams per unit time.
		JavaStreamingContext context = new JavaStreamingContext(conf, Durations.milliseconds(3000));

		Calculator test = new PeakHours();
		test.processData(context);
	}
}
