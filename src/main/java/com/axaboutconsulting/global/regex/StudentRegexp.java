package com.axaboutconsulting.global.regex;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StudentRegexp {
	
	public static final String NAME_REGEXP = "/^[ㄱ-ㅎ가-힣]{1,10}$/";
	public static final String GRADE_REGEXP = "^[1-9]$";
	public static final String TRACK_REGEXP = "/^[ㄱ-ㅎ가-힣]{1,10}$/";
	public static final String TARGET_MAJOR_REGEXP = "/^[ㄱ-ㅎ가-힣]{1,20}$/";
	public static final String TARGET_UNIV_REGEXP = "/^[ㄱ-ㅎ가-힣]{1,20}$/";

}
