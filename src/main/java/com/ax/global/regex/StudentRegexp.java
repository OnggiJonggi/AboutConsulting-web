package com.ax.global.regex;

public class StudentRegexp {
	private StudentRegexp() {}
	
	public static final String NAME_REGEXP = "^[ㄱ-ㅎ가-힣]{1,10}$";
	public static final String GRADE_REGEXP = "^[1-3]$";
	public static final String SEMESTER_REGEXP = "^[1-2]$";
	public static final String TRACK_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣]{1,10}$";
	public static final String TARGET_MAJOR_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣\s/()]{1,30}$";
	public static final String TARGET_UNIV_REGEXP = "^[A-Za-z0-9ㄱ-ㅎ가-힣\s·.,/()]{1,30}$";

}
