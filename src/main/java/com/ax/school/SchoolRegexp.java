package com.ax.school;

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
