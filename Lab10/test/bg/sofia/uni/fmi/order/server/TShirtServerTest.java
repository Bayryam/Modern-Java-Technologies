package bg.sofia.uni.fmi.order.server;

import bg.sofia.uni.fmi.mjt.order.server.TShirtsServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TShirtServerTest {
    @Test
    public void testServerCreation() {
        assertDoesNotThrow(() -> {
            TShirtsServer server = new TShirtsServer();
        });
    }

    @Test
    public void testNewRequestHandlerThreadCreated() throws IOException {
        AtomicInteger initialThreadCount = new AtomicInteger(Thread.activeCount());
        Thread serverThread = new Thread(() -> TShirtsServer.main(null));
        serverThread.start();

        Socket clientSocket = new Socket("localhost", 7777);

        int finalThreadCount = Thread.activeCount();
        clientSocket.close();

        serverThread.interrupt();
        assertEquals(initialThreadCount.incrementAndGet(), finalThreadCount);
    }
}
