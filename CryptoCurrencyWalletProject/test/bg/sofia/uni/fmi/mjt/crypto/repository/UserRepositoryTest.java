package bg.sofia.uni.fmi.mjt.crypto.repository;

import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.exception.UnableToSaveDataException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest {
    private static final UserRepository USER_REPOSITORY = new UserRepository();

    @BeforeAll
    static void setup() throws CipherException, DuplicateUsernameException {
        USER_REPOSITORY.addUser("user", "USA", "0899307374", "ValidPa$sword1");
    }

    @Test
    void testIsUserRegisteredUnregisteredUserReturnsFalse() throws CipherException {
        assertDoesNotThrow(() -> USER_REPOSITORY.isUserRegistered("unregisteredUser", "password"),
            "An exception should not be thrown when checking if an unregistered user is present in the repository.");
        assertFalse(USER_REPOSITORY.isUserRegistered("unregisteredUser", "password"),
            "The unregisteredUser should not be present in the repository.");
    }

    @Test
    void testIsUserRegisteredRegisteredUserReturnsTrue() throws CipherException {
        assertDoesNotThrow(() -> USER_REPOSITORY.isUserRegistered("user", "ValidPa$sword1"),
            "An exception should not be thrown when checking if a registered user is present in the repository.");
        assertTrue(USER_REPOSITORY.isUserRegistered("user", "ValidPa$sword1"),
            "The user should be present in the repository.");
    }

    @Test
    void testAddUserNewUserBePresentInRepository() throws CipherException {
        assertDoesNotThrow(() -> USER_REPOSITORY
            .addUser("unregisteredUser", "location", "08993311", "password"), "" +
            "An exception should not be thrown when adding a new user to the repository.");
        assertTrue(USER_REPOSITORY.isUserRegistered("unregisteredUser", "password"),
            "The new user should be present in the repository.");
    }

    @Test
    void testAddUserWithDuplicateUsernameThrowsDuplicateUsernameException() {
        assertThrows(DuplicateUsernameException.class, () -> USER_REPOSITORY
                .addUser("user", "location", "08993311", "password"),
            "A DuplicateUsernameException should be thrown when adding a user with a duplicate username.");
    }

    @Test
    void testAddUserWithNullParameterThrowsDuplicateUsernameException() {
        assertThrows(IllegalArgumentException.class, () -> USER_REPOSITORY
                .addUser(null, "location", "08993311", "password"),
            "An IllegalArgumentException should be thrown when adding a user with a null username.");
    }

    @Test
    void testSetUpRepositoryWithUsers() throws CipherException {
        Reader reader = new StringReader(
            "[{\"username\":\"user\",\"location\":\"USA\",\"phoneNumber\":" +
                "\"0899307374\",\"password\":\"Ā����\\u0004F�_K�G�[.\",\"cryptoWallet\"" +
                ":{\"totalDeposits\":0,\"totalWithdrawals\":0,\"balance\":0,\"estimatesSet\":[]}}]");
        USER_REPOSITORY.setUpRepository(reader);
        assertTrue(USER_REPOSITORY.isUserRegistered("user", "ValidPa$sword1"),
            "The user should be present in the repository.");
        assertEquals(1, USER_REPOSITORY.getUsers().size());
    }

    @Test
    void testSetUpRepositoryWithNoUsers() {
        Reader reader = new StringReader("");
        USER_REPOSITORY.setUpRepository(reader);
        assertEquals(0, USER_REPOSITORY.getUsers().size(),
            "The repository should be empty.");
    }

    @Test
    void testSaveRepositorySuccessful() {
        String expected = "[{\"username\":\"user\",\"location\":\"USA\",\"phoneNumber\":" +
            "\"0899307374\",\"password\":\"Ā����\\u0004F�_K�G�[.\",\"cryptoWallet\"" +
            ":{\"totalDeposits\":0,\"totalWithdrawals\":0,\"balance\":0,\"estimatesSet\":[]}}]";
        Writer stringWriter = new StringWriter();
        assertDoesNotThrow(() -> USER_REPOSITORY.saveRepository(stringWriter),
            "An exception should not be thrown when saving the repository.");
        assertEquals(expected, stringWriter.toString(),
            "Expected: " + expected + " Actual: " + stringWriter);
    }

    @Test
    void testSaveRepositoryThrowsUnableToSaveDataException() {
        Writer writer = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException();
            }

            @Override
            public void flush() throws IOException {
                throw new IOException();
            }

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        };
        assertThrows(UnableToSaveDataException.class, () -> USER_REPOSITORY.saveRepository(writer),
            "An UnableToSaveDataException should be thrown when saving the repository.");
    }

    @Test
    void testGetUsers() {
        assertEquals(1, USER_REPOSITORY.getUsers().size(),
            "The repository should contain 1 user.");
    }
}

