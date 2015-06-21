package filereceiver;

import driver.Driver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by haw on 20/06/15.
 */

public class SparkBuffer {


    public static void main(String []args){


        System.out.println("Starting SparkBuffer");


        DataInputStream dis;
        PrintStream ps;
        PrintWriter writer;
        BufferedReader in;


        try {
            ServerSocket serverSocket = new ServerSocket(9003);     // Server Socket for the web listener class ( Web Socket)
            ServerSocket serverSocket2 = new ServerSocket(9001);    // Server Socket for the driver class ( Apache Spark Streaming Socket )


            Driver myDriver = new Driver(new SparkBuffer());
            Socket socket = serverSocket.accept();

//            WebListener myWebListener = new WebListener();
            Socket socket2 = serverSocket2.accept();

            dis = new DataInputStream(socket.getInputStream());         // Input stream from the web socket

            writer = new PrintWriter(socket2.getOutputStream());        // Output stream to Spark

            while(true) {
                // read each line from the client and send it to spark Driver
                String str = dis.readLine();
                if(str.length()>1) {
                    System.out.println(str);
                    writer.println(str);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
