package bg.sofia.uni.fmi.mjt.crypto.entity;

import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CryptoWallet {
    private static final int SCALE = 3;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private BigDecimal balance;
    private final Set<Estimate> estimatesSet;

    public CryptoWallet() {
        this.balance = BigDecimal.ZERO;
        this.totalDeposits = BigDecimal.ZERO;
        this.totalWithdrawals = BigDecimal.ZERO;
        this.estimatesSet = new HashSet<>();
    }

    public void deposit(BigDecimal money) throws MoneyOperationException {
        if (money == null) {
            throw new IllegalArgumentException("Money argument is null!");
        }
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyOperationException("Money argument is a non-positive number!");
        }
        balance = balance.add(money);
        totalDeposits = totalDeposits.add(money);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getCurrentProfitLoss() {
        BigDecimal result = balance;
        result = result.add(totalWithdrawals).subtract(totalDeposits);
        return result;
    }

    public BigDecimal withdraw(BigDecimal money) throws MoneyOperationException, InsufficientBalanceException {
        if (money == null) {
            throw new IllegalArgumentException("Money argument is null!");
        }
        subtractMoney(money);
        totalWithdrawals = totalWithdrawals.add(money);
        return money;
    }

    private void subtractMoney(BigDecimal money) throws InsufficientBalanceException, MoneyOperationException {
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyOperationException("Amount argument is a non-positive number!");
        }

        if (balance.compareTo(money) < 0) {
            throw new InsufficientBalanceException("Not enough money in the balance. Current balance: " + getBalance());
        }
        balance = balance.subtract(money);
    }

    public void addEstimate(Estimate estimate, BigDecimal price)
        throws InsufficientBalanceException, MoneyOperationException {
        if (estimate == null) {
            throw new IllegalArgumentException("Estimate argument is null!");
        }

        if (price == null) {
            throw new IllegalArgumentException("Price argument is null!");
        }

        subtractMoney(price);
        estimatesSet.add(estimate);
    }

    public void sellEstimate(String code, BigDecimal currentPrice) {
        if (code == null) {
            throw new IllegalArgumentException("Code argument is null!");
        }

        if (currentPrice == null) {
            throw new IllegalArgumentException("Current price argument is null!");
        }

        for (Estimate estimate : estimatesSet) {
            if (estimate.equity().equityId().equals(code)) {
                balance = balance.add(estimate.amount().multiply(currentPrice).setScale(SCALE, RoundingMode.HALF_UP));
            }
        }
        estimatesSet.removeIf(e -> e.equity().equityId().equals(code));
    }

    public Collection<Estimate> getEstimates() {
        return Collections.unmodifiableCollection(estimatesSet);
    }

    public String getInfo() {
        StringBuilder result = new StringBuilder();
        result.append("Balance: ").append(balance).append("$").append(System.lineSeparator());
        result.append("Equities: ");
        if (estimatesSet.isEmpty()) {
            result.append("{any}");
        } else {
            result.append(System.lineSeparator());
            result.append(
                estimatesSet.stream().map(Estimate::toString).collect(Collectors.joining(System.lineSeparator())));
        }
        return result.toString();
    }
}