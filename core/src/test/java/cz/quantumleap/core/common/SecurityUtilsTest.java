package cz.quantumleap.core.common;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityUtilsTest {

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Test
    public void passwordIsEncoded() {
        String password = "asd";

        String encodedPassword = SecurityUtils.encodePassword(password);

        assertTrue(SecurityUtils.verifyEncodedPassword(password, encodedPassword));
    }

    @Test
    public void messageIsDecrypted() {
        String password = "asd";
        String message = "asd";

        byte[] encrypted = SecurityUtils.encryptMessageByPassword(password, message);
        String decrypted = SecurityUtils.decryptMessageByPassword(password, encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void textIsDecryptedByKey() {
        PrivateKey privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        PublicKey publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        String message = "asd";

        byte[] encrypted = SecurityUtils.encryptMessageByKey(publicKey, message);
        String decrypted = SecurityUtils.decryptMessageByKey(privateKey, encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void signatureIsVerifiedByKey() {
        PrivateKey privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        PublicKey publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        String message = "asd";

        byte[] signature = SecurityUtils.signMessageByKey(privateKey, message);

        assertTrue(SecurityUtils.verifySignatureByKey(publicKey, message, signature));
    }

    private byte[] loadResource(String location) {
        try (InputStream inputStream = resourceResolver.getResource(location).getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
