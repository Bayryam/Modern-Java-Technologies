package bg.sofia.uni.fmi.order.server;

import bg.sofia.uni.fmi.mjt.order.server.ClientRequestHandler;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClientRequestHandlerTest {

    @Test
    void testClientRequestHandlerProcessesRequest() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        String testInput = "request size=S color=RED destination=EUROPE";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        OutputStream mockStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        verify(mockOrderRepository).request("S", "RED", "EUROPE");
    }

    @Test
    void testClientRequestHandlerProcessesGetAll() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        String testInput = "get all";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        OutputStream mockStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        verify(mockOrderRepository).getAllOrders();
    }

    @Test
    void testClientRequestHandlerProcessesGetAllSuccessful() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        String testInput = "get all-successful";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        OutputStream mockStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        verify(mockOrderRepository).getAllSuccessfulOrders();
    }

    @Test
    void testClientRequestHandlerProcessesGetOrderById() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        OutputStream mockOutputStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        String testInput = "get my-order id=1";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        OutputStream mockStream = mock(OutputStream.class);
        when(mockSocket.getOutputStream()).thenReturn(mockStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        verify(mockOrderRepository).getOrderById(1);
    }

    @Test
    void testClientRequestHandlerHandlesUnknownCommand() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        String testInput = "invalid command";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        String output = outputStream.toString();
        assertEquals("Unknown command" + System.lineSeparator(), output);
    }

    @Test
    void testClientRequestHandlerHandlesDisconnect() throws IOException {
        Socket mockSocket = mock(Socket.class);
        OrderRepository mockOrderRepository = mock(OrderRepository.class);

        String testInput = "disconnect";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testInput.getBytes());
        when(mockSocket.getInputStream()).thenReturn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(outputStream);

        ClientRequestHandler handler = new ClientRequestHandler(mockSocket, mockOrderRepository);
        handler.run();

        String output = outputStream.toString();
        assertEquals("Disconnected from the server" + System.lineSeparator(), output);
        Thread handlerThread = new Thread(handler);
        assertFalse(handlerThread.isAlive());
    }


}
