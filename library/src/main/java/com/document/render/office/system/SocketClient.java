
package com.document.render.office.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class SocketClient {
    public static final String HOST = "172.25.3.147";
    public static final int LISTENER_PORT = 3000;

    private static SocketClient sc = new SocketClient();

    private Socket client;


    public SocketClient() {
        initConnection();
    }

    public static SocketClient instance() {
        return sc;
    }


    public void initConnection() {
        try {
            client = new Socket(HOST, LISTENER_PORT);
        } catch (UnknownHostException e) {
            System.out.println("Error setting up socket connection: unknown host at " + HOST
                    + ":" + LISTENER_PORT);
        } catch (IOException e) {
            System.out.println("Error setting up socket connection: " + e);
        }
    }


    public InputStream getFile(String fileName) {
        try {
            OutputStream output = client.getOutputStream();
            output.write(fileName.getBytes());
            output.flush();
            return client.getInputStream();
        } catch (Exception e) {
            System.out.println("Error reading from file: " + fileName);
        }
        return null;
    }
}
