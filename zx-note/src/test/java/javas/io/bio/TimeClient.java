package javas.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *
 *  File: TimeClient.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/20				    zouxiang				Initial.
 *
 * </pre>
 */
public class TimeClient {
    public static void main(String[] args) {
        BufferedReader reader = null;
        PrintWriter writer = null;
        Socket client = null;
        try {
            client = new Socket("127.0.0.1", 8080);
            writer = new PrintWriter(client.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            while (true) {
                writer.println("TIME");
                writer.flush();
                String response = reader.readLine();
                System.out.println("Current Time: " + response);
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
