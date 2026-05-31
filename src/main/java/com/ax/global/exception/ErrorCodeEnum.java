package com.ax.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 예외 처리 코드 저장소
 */
@Getter
public enum ErrorCodeEnum {
	CANNOT_LOGIN(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-001", "로그인이 안 되는데요"),
	CANNOT_CREATE_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-002", "새로운 회원을 생성할 수 없습니다"),
	ID_IS_DUPLICATED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-003", "이미 사용된 아이디입니다"),
	NICKNAME_IS_DUPLICATED(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-004", "이미 사용된 닉네임입니다"),
	
	CANNOT_GRANT_ROLE(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-101", "권한을 부여할 수 없습니다"),
	
	TARGET_REQUIRED(HttpStatus.BAD_REQUEST, "STUDENT-101", "지망 대학/학과가 없습니다"),
	
	RECORD_ANALYZING(HttpStatus.BAD_REQUEST, "RECORD-101", "작업이 진행 중입니다"),
	RECORD_IS_EMPTY(HttpStatus.NOT_FOUND, "RECORD-102", "등록된 생기부가 없어요"),
	RECORD_API_NOT_WORKING(HttpStatus.INTERNAL_SERVER_ERROR, "RECORD-103", "생기부 API가 작동하지 않아요"),
	
	MOCK_IS_EMPTY(HttpStatus.NOT_FOUND, "MOCK-102", "등록된 모의고사가 없어요"),
	MOCK_API_NOT_WORKING(HttpStatus.NOT_FOUND, "MOCK-101", "모의고사 API가 작동하지 않아요"),
	
	FILE_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-101", "파일 메타데이터가 없어요"),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-102", "그런 파일 없어요"),
	DOC_NAME_FORBIDDEN(HttpStatus.BAD_REQUEST, "FILE-103", "서류 이름이 이상해요"),
	DOC_TYPE_FORBIDDEN(HttpStatus.BAD_REQUEST, "FILE-104", "서류 타입이 이상해요");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
	
	// Enum생성자는 직접 작성이 관례
	private ErrorCodeEnum(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}
}
