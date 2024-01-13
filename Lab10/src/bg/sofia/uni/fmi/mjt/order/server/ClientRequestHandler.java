package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientRequestHandler implements Runnable {
    private static final String DELIMITER = " ";
    private static final String REQUEST_COMMAND = "request";
    private static final String GET_COMMAND = "get";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final int COMMAND_INDEX = 0;
    private static final String REQUEST_PATTERN = "^request size=(.*?) color=(.*?) destination=(.*)$";
    private static final String GET_PATTERN = "get (.*?)( |$)((id=)([0-9]+))?";
    private static final int REQUEST_SIZE_ID = 1;
    private static final int REQUEST_COLOR_ID = 2;
    private static final int REQUEST_DESTINATION_ID = 3;
    private static final int GET_SPECIFIC_COMMAND_GROUP_ID = 1;
    private static final int GET_GROUP_ID = 5;
    private static final String GET_ALL = "all";
    private static final String GET_ALL_SUCCESSFUL = "all-successful";
    private static final String GET_MY_ORDER = "my-order";

    private final Socket socket;
    private final OrderRepository repository;

    public ClientRequestHandler(Socket socket, OrderRepository repository) {
        this.socket = socket;
        this.repository = repository;
    }

    private Response processRequestCommand(String line) {
        Pattern r = Pattern.compile(REQUEST_PATTERN);
        Matcher m = r.matcher(line);

        if (m.find()) {
            return repository.request(m.group(REQUEST_SIZE_ID), m.group(REQUEST_COLOR_ID),
                m.group(REQUEST_DESTINATION_ID));
        }
        return null;
    }

    private Response processGetCommand(String line) {
        Pattern r = Pattern.compile(GET_PATTERN);
        Matcher m = r.matcher(line);

        if (m.find()) {
            return switch (m.group(GET_SPECIFIC_COMMAND_GROUP_ID)) {
                case GET_ALL -> repository.getAllOrders();
                case GET_ALL_SUCCESSFUL -> repository.getAllSuccessfulOrders();
                case GET_MY_ORDER -> repository.getOrderById(Integer.parseInt(m.group(GET_GROUP_ID)));
                default -> null;
            };
        }
        return null;
    }

    private Response processCommand(String line) {
        String[] tokens = line.split(DELIMITER);
        return switch (tokens[COMMAND_INDEX]) {
            case REQUEST_COMMAND -> processRequestCommand(line);
            case GET_COMMAND -> processGetCommand(line);
            default -> null;
        };
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals(DISCONNECT_COMMAND)) {
                    out.println("Disconnected from the server");
                    break;
                }

                Response response = processCommand(inputLine);
                if (response == null) {
                    out.println("Unknown command");
                    continue;
                }
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
