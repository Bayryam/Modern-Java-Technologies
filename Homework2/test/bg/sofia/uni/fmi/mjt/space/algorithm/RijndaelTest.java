package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RijndaelTest {
    private final String validTestKey = "0123456789abcdef";
    private final String invalidTestKey = "356789abcdef";

    @Test
    public void testEncryptDecrypt() throws CipherException {
        byte[] inputData = "Test input data".getBytes();

        SecretKey secretKey = new SecretKeySpec(validTestKey.getBytes(), "AES");
        Rijndael rijndael = new Rijndael(secretKey);

        try (ByteArrayOutputStream encryptedOutput = new ByteArrayOutputStream()) {
            rijndael.encrypt(new ByteArrayInputStream(inputData), encryptedOutput);

            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedOutput.toByteArray());
                 ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();
            ) {
                rijndael.decrypt(inputStream, decryptedOutput);

                byte[] decryptedData = decryptedOutput.toByteArray();

                assertEquals(Arrays.toString(inputData), Arrays.toString(decryptedData));
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("There is a problem in the test!", exception);
        }
    }

    @Test
    public void testEncryptWithInvalidKey() {
        byte[] inputData = "Test input data".getBytes();

        SecretKey secretKey = new SecretKeySpec(invalidTestKey.getBytes(), "AES");
        Rijndael rijndael = new Rijndael(secretKey);

        try (ByteArrayOutputStream encryptedOutput = new ByteArrayOutputStream();
             ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
        ) {
            assertThrows(CipherException.class,
                () -> rijndael.encrypt(inputStream, encryptedOutput));
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }

    @Test
    public void testDecryptWithInvalidKey() {
        SecretKey secretKey = new SecretKeySpec(invalidTestKey.getBytes(), "AES");
        Rijndael rijndael = new Rijndael(secretKey);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] {12});
             ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();
        ) {
            assertThrows(CipherException.class, () -> rijndael.decrypt(inputStream, decryptedOutput));
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }

    @Test
    public void testEncryptThrowsCipherException() {
        SecretKey secretKey = new SecretKeySpec(validTestKey.getBytes(), "AES");
        Rijndael rijndael = new Rijndael(secretKey);

        try (InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IOException");
            }
        };
             OutputStream outputStream = new ByteArrayOutputStream();
        ) {
            assertThrows(CipherException.class, () -> rijndael.encrypt(inputStream, outputStream));
        } catch (IOException exception) {
            throw new UncheckedIOException("Error!", exception);
        }
    }

    @Test
    public void testDecryptThrowsCipherException() {
        SecretKey secretKey = new SecretKeySpec(validTestKey.getBytes(), "AES");
        Rijndael rijndael = new Rijndael(secretKey);

        try (InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IOException");
            }
        };
             OutputStream outputStream = new ByteArrayOutputStream();
        ) {
            assertThrows(CipherException.class, () -> rijndael.decrypt(inputStream, outputStream));
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }
}

