package com.ax.consultant.org;

/**
 * 컨설턴트 조직 유효성 검사용 정규식
 */
public class OrgRegexp {
	
	// 상수
	public static final int NAME_MAX_LENGTH = 20;
	
	// 정규식
	public static final String NAME_REGEXP = 
			"^[ㄱ-ㅎ가-힣a-zA-Z0-9.\\-_()@!?,~+=*#\\s]{1," + NAME_MAX_LENGTH + "}$";
	
	private OrgRegexp() {};
}