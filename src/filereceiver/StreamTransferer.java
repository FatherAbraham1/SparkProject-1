package filereceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class StreamTransferer {
    private InputStream input;
    private OutputStream output;
    public  PrintWriter writer;
    public StreamTransferer(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
        writer = new PrintWriter(output);
    }
    public void writeToStream(String line){
        try{
            writer.println(line);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}