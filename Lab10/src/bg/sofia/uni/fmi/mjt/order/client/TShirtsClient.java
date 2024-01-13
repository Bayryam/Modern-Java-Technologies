package bg.sofia.uni.fmi.mjt.order.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TShirtsClient {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final String DISCONNECT_COMMAND = "disconnect";

    public static void main(String[] args) {

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                String message = scanner.nextLine();
                writer.println(message);

                String reply = reader.readLine();
                System.out.println(reply);
                if (DISCONNECT_COMMAND.equals(message)) {
                    break;
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("There is a problem with the network communication", exception);
        }
    }
}
