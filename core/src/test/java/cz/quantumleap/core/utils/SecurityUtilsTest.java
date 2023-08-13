package cz.quantumleap.core.utils;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityUtilsTest {

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Test
    public void passwordIsEncoded() {
        var password = "asd";

        var encodedPassword = SecurityUtils.encodePassword(password);

        assertTrue(SecurityUtils.verifyEncodedPassword(password, encodedPassword));
    }

    @Test
    public void messageIsDecrypted() {
        var password = "asd";
        var message = "asd";

        var encrypted = SecurityUtils.encryptMessageByPassword(password, message);
        var decrypted = SecurityUtils.decryptMessageByPassword(password, encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void textIsDecryptedByKey() {
        var privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        var publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        var message = "asd";

        var encrypted = SecurityUtils.encryptMessageByKey(publicKey, message);
        var decrypted = SecurityUtils.decryptMessageByKey(privateKey, encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void signatureIsVerifiedByKey() {
        var privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        var publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        var message = "asd";

        var signature = SecurityUtils.signMessageByKey(privateKey, message);

        assertTrue(SecurityUtils.verifySignatureByKey(publicKey, message, signature));
    }

    private byte[] loadResource(String location) {
        try (var inputStream = resourceResolver.getResource(location).getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
