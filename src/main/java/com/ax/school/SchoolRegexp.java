package com.ax.school;

/**
 * 학교 유효성 검사용 정규식
 * 
 * 학교 데이터는 나이스에서 가져오니까 학교 등록에 사용하지 않음
 * SanitizeComponent 클래스의 메서드에서 최대 길이 상수만 검색어 소독용으로 씀.
 */
public class SchoolRegexp {
	private SchoolRegexp() {}

	// 최대 길이 상수
	public static final int NAME_MAX_LENGTH = 100;
	public static final int PROVINCE_MAX_LENGTH = 20;
	public static final int SPECIALIZED_MAX_LENGTH = 10;
	public static final int SPECIALIZED_TYPE_MAX_LENGTH = 20;

	// 정규식
	public static final String NAME_REGEXP = "^[가-힣]{1," + NAME_MAX_LENGTH + "}$";
	public static final String PROVINCE_REGEXP = "^[가-힣0-9]{1," + PROVINCE_MAX_LENGTH + "}$";
	public static final String COEDUCATION_REGEXP = "^(남|여|공학)$";
	public static final String SPECIALIZED_REGEXP = "^[가-힣]{1," + SPECIALIZED_MAX_LENGTH + "}$";
	public static final String SPECIALIZED_TYPE_REGEXP = "^[가-힣]{1," + SPECIALIZED_TYPE_MAX_LENGTH + "}$";
}
