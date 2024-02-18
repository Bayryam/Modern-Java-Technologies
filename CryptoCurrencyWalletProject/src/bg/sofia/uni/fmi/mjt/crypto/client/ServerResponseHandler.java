package bg.sofia.uni.fmi.mjt.crypto.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerResponseHandler implements Runnable {
    private final BufferedReader reader;
    private boolean isClientConnected;

    public ServerResponseHandler(BufferedReader reader) {
        this.reader = reader;
        isClientConnected = true;
    }

    public void disconnect() {
        isClientConnected = false;
    }

    @Override
    public void run() {
        String response;
        try {
            while (isClientConnected) {
                if ((response = reader.readLine()) != null) {
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            System.out.println("{ Disconnected } You are disconnected from the server.");
        }
    }
}
