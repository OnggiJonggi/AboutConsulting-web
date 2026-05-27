package com.ax.student.record;

public enum RecordStatusEnum {
	READY, // 분석 시작 전 상태값
	ACTIVE, // 분석 완료
	FAILED,
	EMPTY // JAVA전용, 분석 시도 내역 없음
}
