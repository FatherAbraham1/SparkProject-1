package popularspeech;

import driver.Calculator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 */
public class PopularSpeech extends Calculator implements Serializable
{
	// Hash map that will hold the values of the times and dates.
	static HashMap<Integer, Talk> talkAttendance;
	static boolean isStreaming;

	@Override
	public void processData(JavaStreamingContext c)
	{
		// Set this class's context variable to the received context variable.
		this.context = c;
		isStreaming = true;
		// Initialize the empty hash map of talk ids and talk objects (talk title and attendance count).
		talkAttendance = new HashMap<Integer, Talk>();
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

			// If the preamble (0101010101) was found
			if(!isStreaming)
			{
				System.out.println("Close Connection");
				context.stop(true,true);
				return null;
			}

			return null;
			}
		});

		// Start executing the streams.
		context.start();
//		context.awaitTermination();
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
			if (lineData.equals(PopularSpeech.this.preamble))
				isStreaming = false;
			else
			{
				// Store data tokens. Line format: PERSON_ID,TALK_ID,HANDLE,TALK_TITLE,TRACK,TALK_TIME
				String[] tokens = lineData.split(",",-1);

				Integer talkId = Integer.parseInt(tokens[1]);
				String talkTitle = tokens[3];

				// Get the corresponding value of the talkId. Will return null if it doesn't exist before.
				Talk temp = talkAttendance.get(talkId);

				// This is a new entry for this talk. Create a new Talk object and add it with the id in the hash map.
				if(temp == null)
				{
					// Create new Talk object with the attendance count as 1 (till now) and the talk title.
					temp = new Talk(talkTitle,1);
					talkAttendance.put(talkId,temp);
				}
				// The talk already exists.
				else
					talkAttendance.put(talkId,temp.incrementAttendance());

				// Send the talkId-Talk pair to web-socket in the following format: talkId##talkTitle##talkAttendance
				PopularSpeech.outputFeed.write(talkId + "##" + temp.talkTitle + "##" + talkAttendance);
				PopularSpeech.outputFeed.newLine();
				PopularSpeech.outputFeed.flush();
			}
		}
	}

	/**
	 * A quick and simple class to hold the data of the talks used in this class statistic type. A talk consists of the
	 * talk title and its attendance count. This class also contains a helper function to quickly increment the
	 * attendance count to update the talk object in the hash table.
	 */
	class Talk
	{
		public String talkTitle;
		public int attendance;

		/**
		 * Constructor for the talk object that will be held in the HashMap against its ID. It contains the talk title
		 * and the number of attendees for that talk.
		 * @param title The talk title
		 * @param a The number of attendees in the talk
		 */
		Talk(String title, int a)
		{
			this.talkTitle = title;
			this.attendance = a;
		}

		/**
		 * This handy function quickly increments the attendance of the talk and returns the current talk object. This
		 * is used to quickly update the talk objects in the Hash Map.
		 * @return The current Talk object.
		 */
		public Talk incrementAttendance()
		{
			this.attendance++;
			return this;
		}
	}
}
