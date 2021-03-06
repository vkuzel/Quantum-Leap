package cz.quantumleap.core.common;

import org.apache.commons.lang3.Validate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SecurityUtils {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final int PASSWORD_SALT_LENGTH_BYTES = 16;
    private static final int IV_LENGTH_BYTES = 16;

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verifyEncodedPassword(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

    public static byte[] encryptMessageByPassword(String password, String message) {
        try {
            // SecureRandom.getInstanceStrong() hangs on a machines with too
            // little entropy as generator waits for entropy to initialize.
            // This can lead (and often leads) to request timeouts, etc.
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[PASSWORD_SALT_LENGTH_BYTES];
            secureRandom.nextBytes(salt);
            byte[] iv = new byte[IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = createAesCipher(Cipher.ENCRYPT_MODE, salt, iv, password);
            byte[] encrypted = cipher.doFinal(message.getBytes());
            return EncodingUtils.concatenate(salt, iv, encrypted);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decryptMessageByPassword(String password, byte[] encryptedMessage) {
        try {
            Validate.isTrue(encryptedMessage.length > PASSWORD_SALT_LENGTH_BYTES);
            byte[] salt = Arrays.copyOfRange(encryptedMessage, 0, PASSWORD_SALT_LENGTH_BYTES);
            byte[] iv = Arrays.copyOfRange(encryptedMessage, PASSWORD_SALT_LENGTH_BYTES, PASSWORD_SALT_LENGTH_BYTES + IV_LENGTH_BYTES);
            byte[] encrypted = Arrays.copyOfRange(encryptedMessage, PASSWORD_SALT_LENGTH_BYTES + IV_LENGTH_BYTES, encryptedMessage.length);

            Cipher cipher = createAesCipher(Cipher.DECRYPT_MODE, salt, iv, password);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | InvalidKeySpecException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Cipher createAesCipher(int mode, byte[] salt, byte[] iv, String password) throws InvalidAlgorithmParameterException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, NoSuchAlgorithmException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 1024, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey secretKey = factory.generateSecret(keySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, secretKeySpec, new GCMParameterSpec(128, iv));
        return cipher;
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
