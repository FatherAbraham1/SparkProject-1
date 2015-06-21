package peakhours;

import driver.Calculator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * This class calculates the peak hours of the day by counting the number of times a certain day/hour combination was
 * repeated in the incoming input stream. It saves the data in a hash table in memory and flushes the results to the web
 * socket.
 */
public class PeakHours extends Calculator implements Serializable
{
	// Hash map that will hold the values of the times and dates.
	static HashMap<String, Integer> dateTimeCount;
	static boolean isStreaming;

	@Override
	public void processData(JavaStreamingContext c)
	{
		// Set this class's context variable to the received context variable.
		this.context = c;
		isStreaming = true;
		// Initialize the empty hash map of dates/times and count.
		dateTimeCount = new HashMap<String, Integer>();
		// Create a DStream that will connect to hostname:port, like localhost:9001
		JavaReceiverInputDStream<String> line = context.socketTextStream("localhost", SPARK_PORT);

		// Processing to occur for each line
		line.foreachRDD(new Function<JavaRDD<String>, Void>()
		{
			@Override
			public Void call(JavaRDD<String> stringJavaRDD) throws Exception
			{
				// Process each line in the Java RDD collection. Note that the RDD data type is like a collection.
				stringJavaRDD.foreach(new ProcessLine());


				if(!isStreaming)
					context.stop(true);

				return null;
			}
		});

		// Start executing the streams.
		context.start();
		context.awaitTermination();
	}

	/**
	 * This class implements the logic to process each line of this stream. It will extract the date and time from the
	 * incoming line. It will try looking for them in the hash table, adding the pair if not found or incrementing their
	 * count vice versa.
	 */
	class ProcessLine implements VoidFunction<String>, Serializable
	{
		@Override
		public void call(String lineData) throws Exception
		{

			if (lineData.equals(PeakHours.this.preamble))
				isStreaming = false;
			else
			{
				// Store date time from the input line stream. Line format: SNAPSHOT_TIMESTAMP,TAG_ID,AREA_ID,X,Y,Z
				String dateTime = lineData.split(",")[0];

				// Split the timestamp into date tokens. Timestamp format: Date Time AM/PM
				String[] dateTokens = dateTime.split(" ");
				String day = dateTokens[0];
				String time = dateTokens[1] + " " + dateTokens[2];

				// Get the hours from the time data using the date formatter with the following pattern.
				DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
				Date date = formatter.parse(time);
				int hours = date.getHours();

				// This is the value to use to index the hash table.
				String result = day + " "+hours;
				Integer oldValue = dateTimeCount.get(result);

				// It doesn't exist in the hash table, Add it.
				if (oldValue == null)
					dateTimeCount.put(result, 1);
					// If it does, then increment the value and add it.
				else
					dateTimeCount.put(result, ++oldValue);

				// Send the result-oldValue pair to websocket.
				System.out.println(lineData);

			}
		}
	}
}
