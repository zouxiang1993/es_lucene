package javas.io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

/**
 * <pre>
 *
 *  File: TimeServerHandler.java
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
public class TimeServerHandler implements Runnable {
    private Socket clientProxy;

    public TimeServerHandler(Socket clientProxy) {
        this.clientProxy = clientProxy;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(clientProxy.getInputStream()));
            writer = new PrintWriter(clientProxy.getOutputStream());
            while (true) {
                String request = reader.readLine();
                if (!"TIME".equals(request)) {
                    writer.println("BAD_REQUEST");
                } else {
                    writer.println(Calendar.getInstance().getTime().toLocaleString());
                }
                writer.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
                clientProxy.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
