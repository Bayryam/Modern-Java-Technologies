package bg.sofia.uni.fmi.mjt.crypto.entity;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;

import java.math.BigDecimal;

public record Estimate(BigDecimal amount, Equity equity) {
    @Override
    public String toString() {
        return String.format("Estimate: %s of %s", amount, equity.equityName());
    }

}
