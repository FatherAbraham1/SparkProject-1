package filereceiver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by haw on 20/06/15.
 */
public class TestSender {
    public static void main(String []args){
        Socket socket = null;
        try {
            socket = new Socket("localhost", 9003);
            OutputStream output1 = socket.getOutputStream();
            PrintWriter writer =  new PrintWriter(output1);
            while (true) {
                writer.println("hello");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
