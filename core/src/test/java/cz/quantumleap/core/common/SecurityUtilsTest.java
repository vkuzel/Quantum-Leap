package cz.quantumleap.core.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SecurityUtilsTest {

    @Test
    public void checkEncodedPassword() {
        String password = "asd";
        String encodedPassword = SecurityUtils.encodePassword(password);

        assertTrue(SecurityUtils.checkEncodedPassword(password, encodedPassword));
    }

    @Test
    public void decryptText() {
        String password = "asd";
        String text = "asd";
        byte[] encryptedText = SecurityUtils.encryptText(password, text);
        String decryptedText = SecurityUtils.decryptText(password, encryptedText);

        assertEquals(text, decryptedText);
    }
}
