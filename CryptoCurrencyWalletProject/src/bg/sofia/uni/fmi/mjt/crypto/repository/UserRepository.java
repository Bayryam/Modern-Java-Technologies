package bg.sofia.uni.fmi.mjt.crypto.repository;

import bg.sofia.uni.fmi.mjt.crypto.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.crypto.entity.CryptoWallet;
import bg.sofia.uni.fmi.mjt.crypto.entity.User;
import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;
import bg.sofia.uni.fmi.mjt.crypto.exception.DuplicateUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.exception.UnableToSaveDataException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRepository implements Repository {
    private static final String SECRET_KEY = "ThisIsASecretKey";
    private static final SecretKey KEY = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    private Set<User> users;
    private final Rijndael rijndael = new Rijndael(KEY);
    private static final Gson GSON = new Gson();

    public UserRepository() {
        users = new HashSet<>();
    }

    private String encryptPassword(String password) throws CipherException {
        InputStream inputStream = new ByteArrayInputStream(password.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        rijndael.encrypt(inputStream, outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private boolean isUsernameUnique(String username) {
        List<User> result = users.stream().filter(user -> user.getUsername().equals(username)).toList();
        return result.isEmpty();
    }

    @Override
    public boolean isUserRegistered(String username, String password) throws CipherException {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.verifyPassword(encryptPassword(password))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addUser(String username, String location, String phoneNumber, String password)
        throws DuplicateUsernameException, CipherException {
        if (username == null || location == null || phoneNumber == null || password == null) {
            throw new IllegalArgumentException("One or more of the arguments is null!");
        }
        if (!isUsernameUnique(username)) {
            throw new DuplicateUsernameException("Username with this username already exists!");
        }
        users.add(new User(username, location, phoneNumber, encryptPassword(password), new CryptoWallet()));
    }

    @Override
    public void setUpRepository(Reader reader) {
        var bufferedReader = new BufferedReader(reader);
        String usersData = bufferedReader.lines().collect(Collectors.joining(","));
        Type type = new TypeToken<Set<User>>() {
        }.getType();
        users = GSON.fromJson(usersData, type);
        if (users == null) {
            users = new HashSet<>();
        }
    }

    @Override
    public void saveRepository(Writer writer) throws UnableToSaveDataException {
        var bufferedWriter = new BufferedWriter(writer);
        String usersData = GSON.toJson(users);

        try {
            bufferedWriter.write(usersData);
            bufferedWriter.flush();
        } catch (IOException exception) {
            throw new UnableToSaveDataException("There was a problem while saving the users!", exception);
        }
    }

    @Override
    public Collection<User> getUsers() {
        return users;
    }
}
