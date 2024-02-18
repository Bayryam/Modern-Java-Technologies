package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.exception.ApiClientException;
import bg.sofia.uni.fmi.mjt.crypto.exception.InternalServerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.UnableToSaveDataException;

public class ServerApplication {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start();
        } catch (LoggerException e) {
            System.out.println("Logger exception occurred: " + e.getMessage());
        } catch (UnableToSaveDataException e) {
            System.out.println("Unable to save data exception occurred: " + e.getMessage());
        } catch (ApiClientException e) {
            System.out.println("Api client exception occurred: " + e.getMessage());
        } catch (InternalServerException e) {
            System.out.println("Internal server exception occurred: " + e.getMessage());
        }
    }
}
