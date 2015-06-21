package driver;

import org.apache.spark.streaming.api.java.JavaStreamingContext;

public abstract class Calculator
{
	public int SPARK_PORT = 9003;

	public JavaStreamingContext context;
	public String preamble = "0101";

	/**
	 * This function will house the processing to perform on the stream obtained from the Spark Context object. The
	 * result of the processing will be sent to the web application through a web socket.
	 *
	 * @param context The Spark streaming context object that is used to create streams.
	 */
	public abstract void processData(JavaStreamingContext context);
}
