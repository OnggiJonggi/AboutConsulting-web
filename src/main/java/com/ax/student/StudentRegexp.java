package com.ax.student;

/**
 * 학생 유효성 검사용 정규식
 */
public class StudentRegexp {
	private StudentRegexp() {}

	// 최대 길이 상수
	public static final int NAME_MAX_LENGTH = 10;
	public static final int TRACK_MAX_LENGTH = 10;
	public static final int TARGET_MAJOR_MAX_LENGTH = 30;
	public static final int TARGET_UNIV_MAX_LENGTH = 30;

	// 범위 상수
	public static final int GRADE_MAX = 3;
	public static final int SEMESTER_MAX = 2;

	// 정규식
	public static final String NAME_REGEXP = "^[ㄱ-ㅎ가-힣]{1," + NAME_MAX_LENGTH + "}$";
	public static final String TRACK_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣]{1," + TRACK_MAX_LENGTH + "}$";
	public static final String TARGET_MAJOR_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣\\s/()]{1," + TARGET_MAJOR_MAX_LENGTH + "}$";
	public static final String TARGET_UNIV_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣\\s·.,/()]{1," + TARGET_UNIV_MAX_LENGTH + "}$";
}
