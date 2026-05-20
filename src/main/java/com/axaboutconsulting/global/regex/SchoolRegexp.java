package com.axaboutconsulting.global.regex;

public class SchoolRegexp {
	private SchoolRegexp() {}
	
	public static final String NAME_REGEXP = "^[가-힣]{1,100}$";
	public static final String PROVINCE_REGEXP = "^[가-힣0-9]{1,20}$";
	public static final String COEDUCATION_REGEXP = "^(남|여|공학)$";
	public static final String SPECIALIZED_REGEXP = "^[가-힣]{1,10}$";
	public static final String SPECIALIZED_TYPE_REGEXP = "^[가-힣]{1,20}$";

}
