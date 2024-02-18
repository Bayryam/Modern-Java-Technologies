package bg.sofia.uni.fmi.mjt.crypto.server.command.customary;

import bg.sofia.uni.fmi.mjt.crypto.cache.Cache;
import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import bg.sofia.uni.fmi.mjt.crypto.entity.Estimate;
import bg.sofia.uni.fmi.mjt.crypto.entity.User;
import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.exception.InsufficientBalanceException;
import bg.sofia.uni.fmi.mjt.crypto.exception.LoggerException;
import bg.sofia.uni.fmi.mjt.crypto.exception.MoneyOperationException;
import bg.sofia.uni.fmi.mjt.crypto.repository.UserRepository;
import bg.sofia.uni.fmi.mjt.crypto.server.command.executor.CommandExecutor;
import bg.sofia.uni.fmi.mjt.crypto.server.session.Session;
import bg.sofia.uni.fmi.mjt.crypto.server.session.SessionOrganiser;
import bg.sofia.uni.fmi.mjt.crypto.server.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VerifiedCommandTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final SessionOrganiser organizer = mock(SessionOrganiser.class);
    private final Cache<Equity> equitiesCache = mock(Cache.class);
    private final Logger logger = mock(Logger.class);
    private final SocketChannel socketChannel = mock(SocketChannel.class);
    private final CommandExecutor executor =
        CommandExecutor.configure(userRepository, organizer, equitiesCache, logger);
    private final User user = mock(User.class);
    private final Session session = new Session(socketChannel, null);
    private final Equity bitcoin = new Equity("BTC", "Bitcoin", 1, BigDecimal.valueOf(5));
    private final Estimate estimate = new Estimate(BigDecimal.valueOf(5), bitcoin);

    @BeforeEach
    void setup() {
        when(equitiesCache.getCachedValues()).thenReturn(List.of(bitcoin));
        when(organizer.getUserFromSession(session)).thenReturn(user);
    }

    @Test
    void testFailUnauthenticated() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(false);

        String result = executor.execute("deposit-money 1000", session);

        verify(organizer, times(0)).getUserFromSession(session);
        assertEquals("You are not logged into your account. So you are unable to perform commands!", result,
            "Unauthenticated user should not be able to perform commands!");
    }

    @Test
    void testBuyCommandNonExistingCode() throws LoggerException, MoneyOperationException, InsufficientBalanceException {
        when(organizer.isActiveSession(session)).thenReturn(true);

        String result = executor.execute("buy --offering=TRC --money=1000", session);
        assertEquals("Equity with code TRC does not exist!", result,
            "User should not be able to buy non-existing equity!");
        verify(user, times(0)).buyEquity(any(), any(), any());
    }

    @Test
    void testBuyCommand() throws LoggerException, MoneyOperationException, InsufficientBalanceException {
        when(organizer.isActiveSession(session)).thenReturn(true);

        String result = executor.execute("buy --offering=BTC --money=1000", session);
        assertEquals("Successfully bought 200.0000000000 of Bitcoin", result,
            "User should be able to buy existing equity!");
        verify(user, times(1)).buyEquity(any(), any(), any());
    }

    @Test
    void testSellCommandNotPossessingCrypto() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);

        String result = executor.execute("sell --offering=BTC", session);
        assertEquals("You do not own any BTC!", result,
            "User should not be able to sell non-existing equity!");
        verify(user, times(0)).sellEquity(any(), any());
    }

    @Test
    void testSellCommandExistingCrypto() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);

        String result = executor.execute("sell --offering=GMU", session);
        assertEquals("Equity with code GMU does not exist!", result,
            "User should not be able to sell non-existing equity!");
        verify(user, times(0)).sellEquity(any(), any());
    }

    @Test
    void testSellCommand() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        when(user.getEquities()).thenReturn(List.of(estimate));

        String result = executor.execute("sell --offering=BTC", session);
        assertEquals("Successfully sold BTC!", result,
            "User should be able to sell existing equity!");
        verify(user, times(1)).sellEquity(any(), any());
    }

    @Test
    void testDepositCommand() throws LoggerException, MoneyOperationException {
        when(organizer.isActiveSession(session)).thenReturn(true);

        String result = executor.execute("deposit-money 1000", session);
        assertEquals("Successfully deposited: 1000.0$", result,
            "User should be able to deposit money!");
        verify(user, times(1)).deposit(any());
    }

    @Test
    void testHelpCommand() throws LoggerException {
        String result = executor.execute("help", session);
        assertEquals("""
                You can use the following commands:
                <--> register {username} {password} -> register new user
                <--> login {username} {password} -> login existing user
                <--> logout -> logout current user
                <--> buy --offering={offering_code} --money={amount} -> buy offering with code(offering_code) for money(amount)
                <--> sell --offering=<offering_code> -> sell offering with code(offering_code)
                <--> deposit-money {amount} -> deposit money to your account
                <--> list-offerings - list all offerings
                <--> get-wallet-summary - give information about the active offerings and the money in the wallet
                <--> get-wallet-overall-summary - give information about the all time profit/loss of the user""", result,
            "User should be able to see all available commands!");
    }

    @Test
    void testListOfferingsCommand() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        String result = executor.execute("list-offerings", session);
        assertEquals("Equity[equityId=BTC, equityName=Bitcoin, isCryptoCurrency=1, priceInUSD=5]", result,
            "User should be able to see all available offerings!");
    }

    @Test
    void testLoginCommandAlreadyLoggedIn() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        String result = executor.execute("login name url", session);
        assertEquals("User already logged in.", result,
            "User should not be able to login if already logged in!");
        verify(organizer, never()).registerNewSession(any());
    }

    @Test
    void testLoginCommandInvalidPassword() throws LoggerException, CipherException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        when(userRepository.isUserRegistered("user", "pas")).thenReturn(false);
        String result = executor.execute("login user pas", session);
        assertEquals("Invalid username or password.", result,
            "User should not be able to login with invalid password!");
        verify(organizer, never()).registerNewSession(any());
    }

    @Test
    void testLoginCommandUserRepositoryThrowsCipherException() throws CipherException, LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        when(userRepository.isUserRegistered("user", "pas")).thenThrow(CipherException.class);
        String result = executor.execute("login user pas", session);
        assertEquals("Could not log in. Please try again later.", result,
            "User should not be able to login if user repository throws CipherException!");
        verify(organizer, never()).registerNewSession(any());
    }

    @Test
    void testLoginCommandSuccessful() throws LoggerException, CipherException {
        when(user.getUsername()).thenReturn("user");
        when(organizer.isActiveSession(session)).thenReturn(false);
        when(userRepository.isUserRegistered("user", "pas")).thenReturn(true);
        when(userRepository.getUsers()).thenReturn(List.of(user));
        String result = executor.execute("login user pas", session);
        assertEquals("User user logged successfully", result,
            "User should be able to login with valid credentials!");
        verify(organizer, times(1)).registerNewSession(any());
    }

    @Test
    void testLogoutCommandNotLoggedIn() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        String result = executor.execute("logout", session);
        assertEquals("User not logged in.", result,
            "User should not be able to logout if not logged in!");
        verify(organizer, never()).removeSession(any());
    }

    @Test
    void testLogoutCommandSuccessful() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        when(user.getUsername()).thenReturn("user");
        String result = executor.execute("logout", session);
        assertEquals("Logged out successfully.", result,
            "User should be able to logout if logged in!");
        verify(organizer, times(1)).removeSession(any());
    }

    @Test
    void testOverallSummaryCommand() throws LoggerException {
        when(user.getProfitLoss()).thenReturn(BigDecimal.valueOf(100));
        when(organizer.isActiveSession(session)).thenReturn(true);
        when(user.getInformationForWallet()).thenReturn("info");
        String result = executor.execute("get-wallet-overall-summary", session);
        assertEquals("Profit/Loss: 100$", result,
            "User should be able to see overall summary!");
    }

    @Test
    void testRegisterCommandAlreadyLoggedIn() throws LoggerException, CipherException, DuplicateUsernameException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        String result = executor.execute("register name url phone pass", session);
        assertEquals("Already logged in.", result,
            "User should not be able to register if already logged in!");
        verify(userRepository, never()).addUser(any(), any(), any(), any());
    }

    @Test
    void testRegisterCommandInvalidPassword() throws LoggerException, CipherException, DuplicateUsernameException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        when(userRepository.isUserRegistered("name", "pass")).thenReturn(false);
        String result = executor.execute("register name url phone pass", session);
        assertEquals("The provided password do not follow the restrictions! Password must contain:\n" +
            "- at least 12 characters\n" +
            "- at least one upper case letter\n" +
            "- at least one digit\n" +
            "- at least one special character from the following: .#%!@-$?^&*", result);
        verify(userRepository, never()).addUser(any(), any(), any(), any());
    }

    @Test
    void testRegisterCommandDuplicateUsername() throws LoggerException, CipherException, DuplicateUsernameException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        doThrow(DuplicateUsernameException.class).when(userRepository).addUser(any(), any(), any(), any());
        when(userRepository.isUserRegistered("name", "pass")).thenReturn(false);
        String result = executor.execute("register name url phone ValidPassword123$", session);
        assertEquals("Unable to register user. Username already exists.", result,
            "User should not be able to register with duplicate username!");
    }

    @Test
    void testRegisterCommandCipherException() throws LoggerException, CipherException, DuplicateUsernameException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        doThrow(CipherException.class).when(userRepository).addUser(any(), any(), any(), any());
        when(userRepository.isUserRegistered("name", "pass")).thenReturn(false);
        String result = executor.execute("register name url phone ValidPassword123$", session);
        assertEquals("Unable to register user.", result,
            "User should not be able to register with duplicate username!");
    }

    @Test
    void testRegisterCommandSuccessful() throws LoggerException, CipherException, DuplicateUsernameException {
        when(organizer.isActiveSession(session)).thenReturn(false);
        when(userRepository.isUserRegistered("name", "pass")).thenReturn(false);
        String result = executor.execute("register name url phone ValidPassword123$", session);
        assertEquals("User registered successfully.", result,
            "User should be able to register with valid credentials!");
        verify(userRepository, times(1)).addUser(any(), any(), any(), any());
    }

    @Test
    void testGetWalletSummaryCommand() throws LoggerException {
        when(organizer.isActiveSession(session)).thenReturn(true);
        when(user.getInformationForWallet()).thenReturn("info");
        String result = executor.execute("get-wallet-summary", session);
        assertEquals("info", result,
            "User should be able to see wallet summary!");
    }
}
