package com.ax.student;

/**
 * STUDENT 테이블 STATUS
 */
public enum StudentStatusEnum {
	ACTIVE, // 정상
	SUSPENDED, // 정지
	DELETED, // 삭제
	GRADUATE, // 졸업
	
	;
	
	private StudentStatusEnum() {};
}
