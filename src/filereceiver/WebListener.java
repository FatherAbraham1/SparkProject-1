package filereceiver;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@ServerEndpoint("/messageendpoint")
public class WebListener
{
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	public static Session      thisPeer;
	public static Socket       socket;
	public static PrintWriter  writer;
	public static OutputStream output1;
	public static SparkBuffer  mySparkBuffer;

	static
	{
		System.out.println("<<<<<<<<<<<<<<<<<<<< just one >>>>>>>>>>>>>>>>>>>>>>>>");
		try
		{
			ServerSocket serverSocket = new ServerSocket(9003);     // Socket that communicates with client's socket
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		mySparkBuffer = new SparkBuffer();      // Object that will send data to the Driver class
	}

	@OnMessage
	public String onMessage(String message, Session session)
	{
		System.out.println(">>>>" + message);
		try
		{
			writer.println(message);        // send the message to the Spark Buffer
			writer.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@OnOpen
	public void onOpen(Session peer)
	{
		peers.add(peer);
		SparkBuffer.receiveUserObject(peer);
		try
		{
			socket = new Socket("localhost", 9003);
			output1 = socket.getOutputStream();
			writer = new PrintWriter(output1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		//sendMessage("hello",peer);
	}

	static public void sendMessage(String message, Session session)
	{
		thisPeer = session;
		try
		{
			thisPeer.getBasicRemote().sendText(message + peers.size());
		}
		catch (IOException ex)
		{
		}
	}

	@OnClose
	public void onClose(Session peer)
	{
		peers.remove(peer);
		//System.out.println("bye bye");
	}
}

