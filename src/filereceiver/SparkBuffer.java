package filereceiver;

import driver.Driver;

import javax.websocket.Session;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SparkBuffer
{
	static Session user;

	SparkBuffer()
	{
		System.out.println("Starting SparkBuffer");

		DataInputStream dis;
		PrintStream ps;
		PrintWriter writer;
		BufferedReader in;

		try
		{
			ServerSocket serverSocket2 = new ServerSocket(9001);    // Server Socket for the driver class ( Apache Spark Streaming Socket )

			Driver myDriver = new Driver(user);
			Socket socket = new Socket("localhost", 9003);

			Socket socket2 = serverSocket2.accept();

			dis = new DataInputStream(socket.getInputStream());         // Input stream from the web socket

			writer = new PrintWriter(socket2.getOutputStream());        // Output stream to Spark

			while (true)
			{
				// read each line from the client and send it to spark Driver
				String str = dis.readLine();
				if (str.length() > 1)
				{
					System.out.println(str);
					writer.println(str);
					writer.flush();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	static void receiveUserObject(Session user)
	{
		SparkBuffer.user = user;
	}

}
