package cz.quantumleap.core.common;

import org.apache.commons.lang3.Validate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SecurityUtils {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final int PASSWORD_SALT_LENGTH_BYTES = 16;

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verifyEncodedPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public static byte[] encryptMessageByPassword(String password, String message) {
        try {
            byte[] salt = new byte[PASSWORD_SALT_LENGTH_BYTES];
            SecureRandom.getInstanceStrong().nextBytes(salt);
            byte[] encrypted = Encryptors.stronger(password, new String(Hex.encode(salt))).encrypt(message.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedMessage = Arrays.copyOf(salt, salt.length + encrypted.length);
            System.arraycopy(encrypted, 0, encryptedMessage, salt.length, encrypted.length);
            return encryptedMessage;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decryptMessageByPassword(String password, byte[] encryptedMessage) {
        Validate.isTrue(encryptedMessage.length > PASSWORD_SALT_LENGTH_BYTES);
        byte[] salt = Arrays.copyOfRange(encryptedMessage, 0, PASSWORD_SALT_LENGTH_BYTES);
        byte[] encrypted = Arrays.copyOfRange(encryptedMessage, PASSWORD_SALT_LENGTH_BYTES, encryptedMessage.length);
        byte[] decrypted = Encryptors.stronger(password, new String(Hex.encode(salt))).decrypt(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static PrivateKey createPrivateKey(byte[] pkcs8EncodedKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    public static PublicKey createPublicKey(byte[] x509EncodedKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(x509EncodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param message Max 190 bytes.
     */
    public static byte[] encryptMessageByKey(PublicKey publicKey, String message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(message.getBytes());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decryptMessageByKey(PrivateKey privateKey, byte[] encryptedMessage) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(encryptedMessage);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] signMessageByKey(PrivateKey privateKey, String message) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());
            return signature.sign();
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean verifySignatureByKey(PublicKey publicKey, String message, byte[] signedMessage) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());
            return signature.verify(signedMessage);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            throw new IllegalStateException(e);
        }
    }
}
