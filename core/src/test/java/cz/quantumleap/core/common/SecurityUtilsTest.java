package cz.quantumleap.core.common;

import com.google.common.io.ByteStreams;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SecurityUtilsTest {

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Test
    public void checkEncodedPassword() {
        String password = "asd";

        String encodedPassword = SecurityUtils.encodePassword(password);

        assertTrue(SecurityUtils.verifyEncodedPassword(password, encodedPassword));
    }

    @Test
    public void decryptMessage() {
        String password = "asd";
        String message = "asd";

        byte[] encrypted = SecurityUtils.encryptMessageByPassword(password, message);
        String decrypted = SecurityUtils.decryptMessageByPassword(password, encrypted);

        assertEquals(message, decrypted);
    }

    @Test
    public void decryptTextByKey() {
        PrivateKey privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        PublicKey publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        String message = "asd";

        byte[] encrypted = SecurityUtils.encryptMessageByKey(publicKey, message);
        String decrypted = SecurityUtils.decryptMessageByKey(privateKey, encrypted);

        Assert.assertEquals(message, decrypted);
    }

    @Test
    public void verifySignatureByKey() {
        PrivateKey privateKey = SecurityUtils.createPrivateKey(loadResource("classpath:/security/private.der"));
        PublicKey publicKey = SecurityUtils.createPublicKey(loadResource("classpath:/security/public.der"));
        String message = "asd";

        byte[] signature = SecurityUtils.signMessageByKey(privateKey, message);

        Assert.assertTrue(SecurityUtils.verifySignatureByKey(publicKey, message, signature));
    }

    private byte[] loadResource(String location) {
        try {
            InputStream inputStream = resourceResolver.getResource(location).getInputStream();
            return ByteStreams.toByteArray(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
