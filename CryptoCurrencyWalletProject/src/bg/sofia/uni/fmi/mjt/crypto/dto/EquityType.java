package bg.sofia.uni.fmi.mjt.crypto.dto;

public enum EquityType {
    CRYPTO(1),
    DEFAULT(-1);

    private final int typeCode;

    EquityType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
