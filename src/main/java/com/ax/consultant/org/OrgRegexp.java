package com.ax.consultant.org;

public class OrgRegexp {
	private OrgRegexp() {};
	
	public static final int NAME_MAX_LENGTH = 20;
	public static final String NAME_REGEXP = 
			"^[ㄱ-ㅎ가-힣a-zA-Z0-9.\\-_()@!?,~+=*#\\s]{1," + NAME_MAX_LENGTH + "}$";
	
}