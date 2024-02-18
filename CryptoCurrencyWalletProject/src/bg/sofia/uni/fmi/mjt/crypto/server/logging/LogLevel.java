package bg.sofia.uni.fmi.mjt.crypto.server.logging;

public enum LogLevel {
    INFO(1),
    WARNING(2),
    ERROR(3);

    private final int code;

    LogLevel(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
