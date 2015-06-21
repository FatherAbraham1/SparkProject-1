/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filereceiver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.codec.binary.*;

/**
 *
 * @author Hesham Adel
 * @Date
 */
@ServerEndpoint("/messageendpoint")
public class WebListener {
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    public static Session thisPeer;
    public static Socket socket;
    public static PrintWriter writer;
    public static OutputStream output1;
    @OnMessage
    public String onMessage(String message, Session session) {
        System.out.println(">>>>"+message);
        try {
                writer.println(message);        // send the message to the Spark Buffer
                writer.flush();
            } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @OnOpen
    public void onOpen (Session peer) {
        peers.add(peer);
        try {
            socket = new Socket("localhost", 9003);
            output1 = socket.getOutputStream();
            writer = new PrintWriter(output1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //sendMessage("hello",peer);
    }
    public void sendMessage(String message, Session session){
        thisPeer = session;
        try {
            thisPeer.getBasicRemote().sendText(message + peers.size());
        } catch (IOException ex) {
            Logger.getLogger(WebListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @OnClose
    public void onClose (Session peer) {
        peers.remove(peer);
        //System.out.println("bye bye");

    }
}

