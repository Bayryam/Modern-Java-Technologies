package bg.sofia.uni.fmi.mjt.crypto.repository;

import bg.sofia.uni.fmi.mjt.crypto.entity.User;
import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.exception.UnableToSaveDataException;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

public interface Repository {

    /**
     * Checks if a user is registered.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return true if the user is registered, false otherwise.
     * @throws CipherException if there's an issue with the encryption or decryption process
     */
    boolean isUserRegistered(String username, String password) throws CipherException;

    /**
     * Adds a user.
     *
     * @param username    The username of the user.
     * @param password    The password of the user.
     * @param location    The location of the user.
     * @param phoneNumber The phone number of the user.
     * @throws DuplicateUsernameException If a user with the same username already exists.
     * @throws CipherException if there's an issue with the encryption or decryption process
     */
    void addUser(String username, String password, String location, String phoneNumber)
        throws DuplicateUsernameException, CipherException;

    /**
     * Sets up the repository.
     *
     * @param reader The reader to read the data from.
     */
    void setUpRepository(Reader reader);

    /**
     * Saves the repository.
     *
     * @param writer The writer to write the data to.
     * @throws UnableToSaveDataException If an error occurs during the saving process.
     */
    void saveRepository(Writer writer) throws UnableToSaveDataException;

    /**
     * Retrieves all users.
     *
     * @return a collection of all users.
     */
    Collection<User> getUsers();
}
