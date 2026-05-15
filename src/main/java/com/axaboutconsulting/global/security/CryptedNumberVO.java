package com.axaboutconsulting.global.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 식별번호 등 서버 내부에서 사용하는 식별번호를
 * 뷰 페이지에 보낼 때 암호화해서 사용하는데
 * 암호화할 때 사용해요
 * 응답용 객체에 상속해 써요
 * db조회할 때 컬럼 별칭에 신경쓰기!
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CryptedNumberVO {
	private int number;
	private String encryptedNumber;
}
