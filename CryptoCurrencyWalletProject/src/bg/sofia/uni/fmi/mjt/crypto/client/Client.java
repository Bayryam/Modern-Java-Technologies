package bg.sofia.uni.fmi.mjt.crypto.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final String DISCONNECT_COMMAND = "Disconnect";

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8));) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Scanner scanner = new Scanner(System.in);
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println(
                "{ Connected } Connected to the server." +
                    " If this is your first sight at this software, type help for more info.");

            ServerResponseHandler clientReader = new ServerResponseHandler(reader);
            executor.execute(clientReader);

            communicateWithServer(scanner, writer);
            clientReader.disconnect();
            executor.shutdown();
        } catch (IOException e) {
            System.err.println(
                "{ Error } There was a problem with the connection to the server. Please try again later.");
        }
    }

    private void communicateWithServer(Scanner scanner, BufferedWriter writer) throws IOException {
        while (true) {
            String command = scanner.nextLine();
            if (DISCONNECT_COMMAND.equals(command)) {
                break;
            }
            writer.write(command);
            writer.flush();
        }
    }
}
