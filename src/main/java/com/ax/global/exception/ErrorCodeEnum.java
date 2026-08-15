package com.ax.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 예외 처리 코드 저장소
 * 
 * code
 * 000번대 : 일반
 * 100번대 : 권한 / 특수
 * 200번대 : API / 기타
 */
@Getter
public enum ErrorCodeEnum {
	
	/**
	 * MEMBER
	 */
	CANNOT_LOGIN(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-001", "로그인이 안 되는데요"),
	CANNOT_CREATE_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-002", "새로운 회원을 생성할 수 없습니다"),
	ID_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER-003", "이미 사용된 아이디입니다"),
	NICKNAME_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER-004", "이미 사용된 닉네임입니다"),
	FAILED_CREATE_ACCOUNT(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-005", "신규 계정 생성에 실패했습니다"),
	FAILED_UPDATE_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-006", "회원 정보 수정에 실패했습니다"),
	
	CANNOT_UPDATE_MY_STATUS(HttpStatus.FORBIDDEN, "MEMBER-101", "자신의 상태는 수정할 수 없습니다"),
	
	
	/**
	 * ROLE
	 */
	FAILED_GRANT_ROLE(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE-001", "권한 부여에 실패했습니다"),
	FAILED_DELETE_ROLE(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE-002", "권한을 삭제에 실패했습니다"),
	FAILED_CREATE_CONSULTANT_LEADER(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE-003", "대표 컨설턴트 권한 추가에 실패했습니다"),
	
	CANNOT_CREATE_SUPER_ADMIN(HttpStatus.FORBIDDEN, "ROLE-101", "최고 관리자 생성 권한이 없습니다"),
	CANNOT_CREATE_ADMIN(HttpStatus.FORBIDDEN, "ROLE-102", "관리자 생성 권한이 없습니다."),
	CANNOT_CHANGE_MY_ROLE(HttpStatus.FORBIDDEN, "ROLE-103", "자신의 권한은 수정할 수 없습니다"),
	CANNOT_DELETE_ADMIN_ACCOUNT(HttpStatus.FORBIDDEN, "ROLE-104", "관리자 계정은 삭제할 수 없습니다."),

	
	/**
	 * STUDENT
	 */
	CANNOT_FIND_STUDENT(HttpStatus.NOT_FOUND, "STUDENT-001", "학생을 찾을 수 없습니다"),
	FAILED_UPDATE_STUDENT(HttpStatus.NOT_FOUND, "STUDENT-002", "학생정보 수정에 실패했습니다"),
	FAILED_UPDATE_STUDENT_STATUS(HttpStatus.NOT_FOUND, "STUDENT-003", "학생 상태 수정에 실패했습니다"),
	
	NOT_YOUR_STUDENT(HttpStatus.FORBIDDEN, "STUDENT-101", "해당 컨설턴트에 배정된 학생이 아닙니다"),
	TARGET_REQUIRED(HttpStatus.BAD_REQUEST, "STUDENT-101", "지망 대학/학과가 없습니다"),
	
	/**
	 * RECORD
	 */
	RECORD_IS_EMPTY(HttpStatus.NOT_FOUND, "RECORD-001", "등록된 생기부가 없어요"),
	FAILED_CREATE_RECORD_GROUP(HttpStatus.INTERNAL_SERVER_ERROR, "RECORD-002", "생기부 그룹 생성에 실패했습니다"),
	RECORD_ANALYZING(HttpStatus.BAD_REQUEST, "RECORD-002", "작업이 진행 중입니다"),
	
	/**
	 * MOCK
	 */
	MOCK_IS_EMPTY(HttpStatus.NOT_FOUND, "MOCK-001", "등록된 모의고사가 없어요"),
	FAILED_CREATE_MOCK_GROUP(HttpStatus.INTERNAL_SERVER_ERROR, "MOCK-002", "모의고사 그룹 생성에 실패했습니다"),
	
	/**
	 * CONSULTANT
	 */
	CONSULTANT_STUDENT_IS_DUPLICATED(HttpStatus.BAD_REQUEST, "CONSULTANT-001", "해당 학생은 이미 배정되었습니다"),
	FAILED_CREATE_CONSULTANT_STUDENT(HttpStatus.BAD_REQUEST, "CONSULTANT-002", "컨설턴트-학생 배정에 실패했습니다"),
	FAILED_DELETE_CONSULTANT_STUDENT(HttpStatus.BAD_REQUEST, "CONSULTANT-003", "컨설턴트-학생 삭제를 실패했습니다"),
	
	/**
	 * ORG
	 */
	NO_ORG_LEADER_OR_CONSULTANT(HttpStatus.FORBIDDEN, "ORG-001", "조직 리더 혹은 구성원이 없습니다"),
	ALREADY_CHARGED(HttpStatus.FORBIDDEN, "ORG-002", "컨설턴트가 이미 소속된 조직이 있습니다"),
	ORG_NAME_IS_DUPLICATED(HttpStatus.FORBIDDEN, "ORG-003", "이미 사용된 조직 이름입니다"),
	FAILED_CREATE_ORG(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-004", "조직 생성에 실패했습니다"),
	FAILED_UPDATE_CONSULTANT_ORG_NO(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-005", "컨설턴트를 생성된 조직에 삽입하는데 실패했습니다"),
	FAILED_UPDATE_ORG_NAME(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-006", "조직 이름 변경에 실패했습니다"),
	FAILED_UPDATE_ORG_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-007", "조직 상태값 변경에 실패했습니다"),
	NO_ORG_LEADER(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-008", "조직 리더가 없어요"),
	FAILED_UPDATE_ORG_LEADER(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-009", "조직 대표 변경에 실패했습니다"),
	FAILED_DELETE_OLD_ORG_LEADER_ROLE(HttpStatus.INTERNAL_SERVER_ERROR, "ORG-010", "조직 기존 대표 권한 삭제에 실패했습니다"),
	
	NOT_SAME_ORG(HttpStatus.FORBIDDEN, "ORG-101", "같은 소속이 아닙니다"),
	
	
	/**
	 * FILE
	 */
	REQUEST_FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "FILE-001", "저장 요청한 파일이 없어요"),
	FAILED_CREATE_FILE_INFO(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-002", "파일 메타데이터 저장에 실패했습니다"),
	FAILED_CREATE_FILE_HISTORY(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-003", "파일 메타데이터 기록 저장에 실패했습니다"),
	
	
	/**
	 * 에러페이지 실험용
	 */
	DUMMY_ERROR_CODE_4XX(HttpStatus.TOO_MANY_REQUESTS, "DUMMY-4XX", "실험용 오류 코드 - 4XX"),
	DUMMY_ERROR_CODE_400(HttpStatus.BAD_REQUEST, "DUMMY-400", "실험용 오류 코드 - 400"),
	DUMMY_ERROR_CODE_401(HttpStatus.UNAUTHORIZED, "DUMMY-401", "실험용 오류 코드 - 401"),
	DUMMY_ERROR_CODE_403(HttpStatus.FORBIDDEN, "DUMMY-403", "실험용 오류 코드 - 403"),
	DUMMY_ERROR_CODE_404(HttpStatus.NOT_FOUND, "DUMMY-404", "실험용 오류 코드 - 404"),
	DUMMY_ERROR_CODE_405(HttpStatus.METHOD_NOT_ALLOWED, "DUMMY-405", "실험용 오류 코드 - 405"),
	DUMMY_ERROR_CODE_5XX(HttpStatus.BAD_GATEWAY, "DUMMY-5XX", "실험용 오류 코드 - 5XX"),
	DUMMY_ERROR_CODE_500(HttpStatus.INTERNAL_SERVER_ERROR, "DUMMY-500", "실험용 오류 코드 - 500"),
	DUMMY_ERROR_CODE_ERROR(HttpStatus.OK, "DUMMY-ERROR", "실험용 오류 코드 - ERROR"),
	
	;
	
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
