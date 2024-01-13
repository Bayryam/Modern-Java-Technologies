package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TShirtsServer {

    private static final int SERVER_PORT = 7777;
    private static final int MAX_EXECUTOR_THREADS = 100000;

    public static void main(String[] args) {

        //ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();
        try (ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS)) {
            OrderRepository repository = new MJTOrderRepository();

            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                Socket clientSocket;
                while (true) {
                    clientSocket = serverSocket.accept();
                    ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket, repository);
                    executor.execute(clientHandler);
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("There is a problem with the server socket", exception);
        }
    }
}
