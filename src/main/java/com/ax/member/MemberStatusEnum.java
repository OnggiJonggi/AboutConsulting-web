package com.ax.member;

/**
 * MEMBER 테이블 STATUS
 */
public enum MemberStatusEnum {
	ACTIVE, // 정상
	SUSPENDED, // 정지
	DELETED, // 삭제
	;
	
	private MemberStatusEnum() {};
}