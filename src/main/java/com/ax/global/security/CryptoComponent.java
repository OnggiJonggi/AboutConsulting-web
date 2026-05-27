package com.ax.global.security;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES-CBC 128
 */
@Component
public class CryptoComponent {

	// key.properties에서 뽑아오기
	@Value("${crypto.key}")
	private String keyStr;

	/**
	 * 암호문 생성 로직
	 * @param plainText
	 * @return base64safeurl
	 * @throws Exception
	 */
	public String encrypt(String plainText) throws Exception {
		// 시큐어랜덤으로 iv생성
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);

		// 암호화
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

		// iv와 암호문을 합쳐 base64
		byte[] combined = new byte[iv.length + encrypted.length];
		System.arraycopy(iv, 0, combined, 0, iv.length);
		System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

		return Base64.getUrlEncoder().encodeToString(combined);
	}

	/**
	 * 암호문 복호 로직
	 * @param encryptedText
	 * @return 평문
	 * @throws Exception
	 */
	public String decrypt(String encryptedText) throws Exception {
		// base64safeurl 디코딩
		byte[] combined = Base64.getUrlDecoder().decode(encryptedText);

		// iv와 암호문 분리
		byte[] iv = new byte[16];
		byte[] encrypted = new byte[combined.length - 16];
		System.arraycopy(combined, 0, iv, 0, 16);
		System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

		// 복호화
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes("UTF-8"), "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

		return new String(cipher.doFinal(encrypted), "UTF-8");
	}
	
	/**
	 * number를 암호화하기
	 * @param <T CryptedNumberVO>
	 * @param list
	 * @throws Exception
	 */
	public <T extends CryptedNumberVO> void encryptList(List<T> list) throws Exception{
		for(CryptedNumberVO detail : list) {
			detail.setEncryptedNumber(encrypt(String.valueOf(detail.getNumber())));
			detail.setNumber(0);
		}
	}

}
