package cz.quantumleap.core.common;

import org.apache.commons.lang3.Validate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class SecurityUtils {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final int PASSWORD_SALT_LENGTH_BYTES = 16;

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean checkEncodedPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public static byte[] encryptText(String password, String text) {
        try {
            byte[] salt = new byte[PASSWORD_SALT_LENGTH_BYTES];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            byte[] encrypted = Encryptors.stronger(password, new String(Hex.encode(salt))).encrypt(text.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedText = Arrays.copyOf(salt, salt.length + encrypted.length);
            System.arraycopy(encrypted, 0, encryptedText, salt.length, encrypted.length);
            return encryptedText;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decryptText(String password, byte[] encryptedByteArray) {
        Validate.isTrue(encryptedByteArray.length > PASSWORD_SALT_LENGTH_BYTES);
        byte[] salt = Arrays.copyOfRange(encryptedByteArray, 0, PASSWORD_SALT_LENGTH_BYTES);
        byte[] encrypted = Arrays.copyOfRange(encryptedByteArray, PASSWORD_SALT_LENGTH_BYTES, encryptedByteArray.length);
        byte[] decrypted = Encryptors.stronger(password, new String(Hex.encode(salt))).decrypt(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
