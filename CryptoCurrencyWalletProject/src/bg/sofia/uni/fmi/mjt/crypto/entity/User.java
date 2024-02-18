package bg.sofia.uni.fmi.mjt.crypto.entity;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;

import com.google.gson.Gson;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.Objects;

public class User {
    private static final Gson GSON = new Gson();
    private final String username;
    private final String location;
    private final String phoneNumber;
    private final String password;
    private final CryptoWallet cryptoWallet;

    public User(String username, String location, String phoneNumber, String password, CryptoWallet cryptoWallet) {
        this.username = username;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.cryptoWallet = cryptoWallet;
    }

    public static User of(String line) {
        return GSON.fromJson(line, User.class);
    }

    public BigDecimal getProfitLoss() {
        return cryptoWallet.getCurrentProfitLoss();
    }

    public String getUsername() {
        return username;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public BigDecimal getBalance() {
        return cryptoWallet.getBalance();
    }

    public Collection<Estimate> getEquities() {
        return cryptoWallet.getEstimates();
    }

    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public void buyEquity(Equity equity, BigDecimal amount, BigDecimal price)
        throws InsufficientBalanceException, MoneyOperationException {
        cryptoWallet.addEstimate(new Estimate(amount, equity), price);
    }

    public void sellEquity(String code, BigDecimal currentPrice) {
        cryptoWallet.sellEstimate(code, currentPrice);
    }

    public void deposit(BigDecimal amount) throws MoneyOperationException {
        cryptoWallet.deposit(amount);
    }

    public String getInformationForWallet() {
        return cryptoWallet.getInfo();
    }

    @Override
    public String toString() {
        return "User{" +
            "username='" + username + '\'' +
            ", location='" + location + '\'' +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", cryptoWallet=" + cryptoWallet +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
