package bg.sofia.uni.fmi.mjt.crypto.client;

import java.util.Scanner;

public class ClientApplication {
    private static final String CONNECT_CMD = "Connect";
    private static final String DISCONNECT_CMD = "Disconnect";
    private static final String EXIT_COMMAND = "Exit";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Cryptocurrency Wallet Application!");
        while (true) {
            System.out.print(
                "->Connect to the server: Connect\n->Disconnect from the server: Disconnect\n->Exit: Exit\n");
            String command = scanner.nextLine();
            if (isCommandValid(command) && command.startsWith(CONNECT_CMD)) {
                Client client = new Client();
                client.start();
            } else if (isCommandValid(command) && command.startsWith(DISCONNECT_CMD)) {
                System.out.println("You are not connected to the server.");
            } else if (isCommandValid(command) && command.equals(EXIT_COMMAND)) {
                System.out.print("You are currently exiting the application. Goodbye!");
                break;
            } else {
                System.out.println("Invalid command.");
            }
        }
    }

    private static boolean isCommandValid(String command) {
        return command.equals(CONNECT_CMD) || command.equals(DISCONNECT_CMD) || command.equals(EXIT_COMMAND);
    }
}