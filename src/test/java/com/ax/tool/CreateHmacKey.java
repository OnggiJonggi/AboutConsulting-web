package com.ax.tool;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Hmac용 무작위 시크릿 키 생성
 */
public class CreateHmacKey {
	
    @Test
    public void run() throws Exception {
        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);
        String secret = java.util.Base64.getEncoder().encodeToString(key);

        System.out.println("생성된 비밀키: " + secret);

        // Assertions : 유효성 검사
        assertNotNull(secret);
        assertTrue(secret.length() > 0);
    }
}
