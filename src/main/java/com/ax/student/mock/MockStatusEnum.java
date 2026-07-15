package com.ax.student.mock;

/**
 * MOCK_GROUP 테이블 STATUS
 */
public enum MockStatusEnum {
	READY, // 분석 시작 전 상태값
	ACTIVE, // 분석 완료
	FAILED, // 실 패 !
	EMPTY // JAVA전용. 분석 시도 내역 없음
	;
	
	private MockStatusEnum() {};
}
