package bg.sofia.uni.fmi.mjt.crypto.entity;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptoWalletTest {
    private final CryptoWallet wallet = new CryptoWallet();
    private final Equity bitcoin = new Equity("BTC", "Bitcoin", 1, BigDecimal.valueOf(5));
    private final Estimate estimate = new Estimate(BigDecimal.valueOf(5), bitcoin);

    @Test
    void testDepositValidDeposit() throws MoneyOperationException {
        wallet.deposit(BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(100), wallet.getBalance(),
            "The balance should be 100 after depositing 100.");
    }

    @Test
    void testDepositNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(null),
            "An IllegalArgumentException should be thrown when depositing null money.");
    }

    @Test
    void testDepositZeroDepositThrowsMoneyOperationException() {
        assertThrows(MoneyOperationException.class, () -> wallet.deposit(BigDecimal.ZERO),
            "A MoneyOperationException should be thrown when depositing zero money.");
    }

    @Test
    void testDepositNegativeDepositNegativeAmountThrowsMoneyOperationException() {
        assertThrows(MoneyOperationException.class, () -> wallet.deposit(BigDecimal.valueOf(-1)),
            "A MoneyOperationException should be thrown when depositing negative money.");
    }

    @Test
    void testGetCurrentProfitLoss() throws MoneyOperationException, InsufficientBalanceException {
        wallet.deposit(BigDecimal.valueOf(100));
        wallet.withdraw(BigDecimal.valueOf(50));
        wallet.addEstimate(estimate, BigDecimal.valueOf(25));
        wallet.sellEstimate("BTC", BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(475), wallet.getCurrentProfitLoss(),
            "The current profit/loss should be 475 after depositing 100 and withdrawing 50. " +
                "Then buying 5 BTC for 25 and selling them for 100.");
    }

    @Test
    void testWithdrawValidWithdraw() throws MoneyOperationException, InsufficientBalanceException {
        wallet.deposit(BigDecimal.valueOf(100));
        wallet.withdraw(BigDecimal.valueOf(50));
        assertEquals(BigDecimal.valueOf(50), wallet.getBalance(),
            "The balance should be 50 after withdrawing 50 from 100.");
    }

    @Test
    void testWithdrawNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(null),
            "An IllegalArgumentException should be thrown when withdrawing null money.");
    }

    @Test
    void testWithdrawZeroWithdrawThrowsMoneyOperationException() {
        assertThrows(MoneyOperationException.class, () -> wallet.withdraw(BigDecimal.ZERO),
            "A MoneyOperationException should be thrown when withdrawing zero money.");
    }

    @Test
    void testWithdrawNegativeWithdrawThrowsMoneyOperationException() {
        assertThrows(MoneyOperationException.class, () -> wallet.withdraw(BigDecimal.valueOf(-1)),
            "A MoneyOperationException should be thrown when withdrawing negative money.");
    }

    @Test
    void testWithdrawInsufficientBalanceThrowsInsufficientBalanceException() {
        assertThrows(InsufficientBalanceException.class, () -> wallet.withdraw(BigDecimal.valueOf(1)),
            "An InsufficientBalanceException should be thrown when withdrawing more money than the balance.");
    }

    @Test
    void testAddEstimateValidAddEstimate() throws MoneyOperationException, InsufficientBalanceException {
        wallet.deposit(BigDecimal.valueOf(100));
        wallet.addEstimate(estimate, BigDecimal.valueOf(25));
        assertEquals(BigDecimal.valueOf(75), wallet.getBalance(),
            "The balance should be 75 after depositing 100 and buying 5 BTC for 25.");
    }

    @Test
    void testAddEstimateInsufficientBalanceThrowsInsufficientBalanceException() {
        assertThrows(InsufficientBalanceException.class, () -> wallet.addEstimate(estimate, BigDecimal.valueOf(101)),
            "An InsufficientBalanceException should be thrown when buying an estimate with a price higher than the balance.");
    }

    @Test
    void testAddEstimateNegativePriceThrowsMoneyOperationException() {
        assertThrows(MoneyOperationException.class, () -> wallet.addEstimate(estimate, BigDecimal.valueOf(-1)),
            "A MoneyOperationException should be thrown when buying an estimate with a negative price.");
    }

    @Test
    void testAddEstimateNullEstimateThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.addEstimate(null, BigDecimal.valueOf(25)),
            "An IllegalArgumentException should be thrown when buying an estimate with a null estimate.");
    }

    @Test
    void testAddEstimateNullPriceThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.addEstimate(estimate, null),
            "An IllegalArgumentException should be thrown when buying an estimate with a null price.");
    }

    @Test
    void testSellEstimateValidSellEstimate() throws MoneyOperationException, InsufficientBalanceException {
        wallet.deposit(BigDecimal.valueOf(100));
        wallet.addEstimate(estimate, BigDecimal.valueOf(25));
        wallet.sellEstimate("BTC", BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(575), wallet.getBalance(),
            "The balance should be 575 after depositing 100, buying 5 BTC for 25 and selling them for 100.");
    }

    @Test
    void testSellEstimateNullCodeThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.sellEstimate(null, BigDecimal.valueOf(100)),
            "An IllegalArgumentException should be thrown when selling an estimate with a null code.");
    }

    @Test
    void testSellEstimateNullPriceThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> wallet.sellEstimate("BTC", null),
            "An IllegalArgumentException should be thrown when selling an estimate with a null price.");
    }

    @Test
    void testGetInfoHasEstimates() throws MoneyOperationException, InsufficientBalanceException {
        String expected = "Balance: 75$" + System.lineSeparator() +
            "Equities: " + System.lineSeparator() +
            "Estimate: 5 of Bitcoin";
        wallet.deposit(BigDecimal.valueOf(100));
        wallet.addEstimate(estimate, BigDecimal.valueOf(25));
        assertEquals(expected, wallet.getInfo());
    }

    @Test
    void testGetInfoNoEstimates() throws MoneyOperationException, InsufficientBalanceException {
        String expected = "Balance: 100$" + System.lineSeparator() +
            "Equities: {any}";
        wallet.deposit(BigDecimal.valueOf(100));
        assertEquals(expected, wallet.getInfo(), "Expected: " + expected + " Actual: " + wallet.getInfo());
    }

}
