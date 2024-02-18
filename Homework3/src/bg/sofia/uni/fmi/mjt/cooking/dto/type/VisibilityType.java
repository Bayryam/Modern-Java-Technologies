package bg.sofia.uni.fmi.mjt.cooking.dto.type;

public enum VisibilityType {
    PUBLIC("public"),
    USER("user"),
    ANY("any");

    private final String value;

    VisibilityType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}