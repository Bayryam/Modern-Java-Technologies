package bg.sofia.uni.fmi.mjt.crypto.algorithm;

import bg.sofia.uni.fmi.mjt.crypto.exception.CipherException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Rijndael implements SymmetricBlockCipher {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KILOBYTE = 1024;
    private static final int OFFSET = 0;
    private static final int FILE_END = -1;
    private final SecretKey secretKey;

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        Cipher cipher = initialiseCipher(Cipher.ENCRYPT_MODE);

        try {
            CipherOutputStream writeStream = new CipherOutputStream(outputStream, cipher);
            byte[] buffer = new byte[KILOBYTE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != FILE_END) {
                writeStream.write(buffer, OFFSET, bytesRead);
            }

            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                outputStream.write(finalBlock);
            }
        } catch (IOException | IllegalBlockSizeException | BadPaddingException exception) {
            throw new CipherException("There is a problem with the encrypting!", exception);
        }
    }

    @Override
    public void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {
        Cipher cipher = initialiseCipher(Cipher.DECRYPT_MODE);

        try {
            OutputStream writeStream = new CipherOutputStream(outputStream, cipher);

            byte[] buffer = new byte[KILOBYTE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != FILE_END) {
                writeStream.write(buffer, OFFSET, bytesRead);
            }

            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null && finalBlock.length > 0) {
                outputStream.write(finalBlock);
            }

        } catch (IOException | IllegalBlockSizeException | BadPaddingException exception) {
            throw new CipherException("There is a problem with the encrypting!", exception);
        }
    }

    private Cipher initialiseCipher(int mode) throws CipherException {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(mode, secretKey);
            return cipher;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException exception) {
            throw new CipherException("There is a problem with the cipher!", exception);
        }
    }
}