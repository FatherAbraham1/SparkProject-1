package crowdedarea;

import driver.Calculator;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.io.Serializable;
import java.util.HashMap;

public class CrowdedArea extends Calculator implements Serializable
{
	// Hash map that will hold the values of the times and dates.
	static HashMap<String, Integer> areaCount;
	static boolean isStreaming;

	@Override
	public void processData(JavaStreamingContext c)
	{
		// Set this class's context variable to the received context variable.
		this.context = c;
		isStreaming = true;
		// Initialize the empty hash map of talk ids and talk objects (talk title and attendance count).
		areaCount = new HashMap<String, Integer>();
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

				// If the preamble (010101) was found
				if(!isStreaming)
					context.stop(false,true);

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
			if (lineData.equals(CrowdedArea.this.preamble))
				isStreaming = false;
			else
			{
				// SNAPSHOT_TIMESTAMP,TAG_ID,AREA_ID,X,Y,Z
				String[] tokens = lineData.split(",",-1);
				String areaID = tokens[2];
				Integer oldValue = areaCount.get(areaID);

				// It doesn't exist in the hash table, Add it.
				if(oldValue == null)
				{
					areaCount.put(areaID,1);
					oldValue = 1;
				}
				else
					areaCount.put(areaID,++oldValue);

				// Send the result-oldValue pair to client in the following format: areaID##numberOfPeople
				CrowdedArea.outputFeed.write(areaID + "##" + oldValue);
				CrowdedArea.outputFeed.newLine();
				CrowdedArea.outputFeed.flush();
			}
		}
	}
}
