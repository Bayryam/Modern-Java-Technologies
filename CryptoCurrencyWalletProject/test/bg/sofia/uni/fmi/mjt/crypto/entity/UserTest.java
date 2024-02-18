package bg.sofia.uni.fmi.mjt.crypto.entity;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    User user = new User("username", "location", "phoneNumber", "password", new CryptoWallet());
    Equity toBuy = new Equity("BTC", "Bitcoin", 1, BigDecimal.valueOf(15));

    @BeforeEach
    void setUp() throws MoneyOperationException {
        user.deposit(BigDecimal.valueOf(100));
    }

    @Test
    void testBuyEquity() throws MoneyOperationException, InsufficientBalanceException {
        user.buyEquity(toBuy, BigDecimal.valueOf(10), BigDecimal.valueOf(15));
        assertEquals(1, user.getEquities().size());
    }

    @Test
    void testSellEquityOnlyOneEstimate() throws MoneyOperationException, InsufficientBalanceException {
        user.buyEquity(toBuy, BigDecimal.valueOf(10), BigDecimal.valueOf(15));
        user.sellEquity("BTC", BigDecimal.valueOf(10));
        assertEquals(0, user.getEquities().size());
    }

    @Test
    void testSellEquityMultipleEstimates() throws MoneyOperationException, InsufficientBalanceException {
        user.buyEquity(toBuy, BigDecimal.valueOf(10), BigDecimal.valueOf(15));
        user.buyEquity(toBuy, BigDecimal.valueOf(10), BigDecimal.valueOf(15));
        user.sellEquity("BTC", BigDecimal.valueOf(10));
        assertEquals(0, user.getEquities().size());
    }
}
