package com.ax.global.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HmacComponent {
	private final SecretKeySpec keySpec;
	
	// SecretKeySpec 객체 생성
    public HmacComponent(@Value("${hmac.key}") String keyStr) {
        this.keySpec = new SecretKeySpec(
                keyStr.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
	
	/**
	 * 평문을 HMAC해싱 문자열로 변환
	 */
	public String hashing(String plainText) throws Exception{
		
        // HMAC-SHA256 인스턴스 생성
        Mac mac = Mac.getInstance("HmacSHA256");

        // 비밀키 설정
        mac.init(keySpec);

        // 해싱
        byte[] hash = mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Base64 safe url 인코딩
		return Base64.getUrlEncoder().encodeToString(hash);
	}
}
