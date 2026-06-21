package com.ax.tool;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.ax.global.security.CryptoComponent;

import lombok.RequiredArgsConstructor;

/**
 * 암호화/복호화 실험
 */
@RequiredArgsConstructor
public class CryptoTest {
	private CryptoComponent cryptoComponent;
	
	@Test
	public void enc() throws Exception{
		Scanner sc = new Scanner(System.in);
		String raw = sc.nextLine();
		String enc = cryptoComponent.encrypt(raw);
		System.out.println(enc);
		sc.close();
	}
	
	@Test
	public void dec() throws Exception{
		Scanner sc = new Scanner(System.in);
		String raw = sc.nextLine();
		String dec = cryptoComponent.decrypt(raw);
		System.out.println(dec);
		sc.close();
	}
}
