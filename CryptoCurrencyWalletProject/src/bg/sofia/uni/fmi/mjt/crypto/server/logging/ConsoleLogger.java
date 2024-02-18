package bg.sofia.uni.fmi.mjt.crypto.server.logging;

public class ConsoleLogger implements Loggable {
    @Override
    public void log(String message) {
        System.out.print(message);
    }
}
