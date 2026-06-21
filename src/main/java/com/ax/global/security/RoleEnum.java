package com.ax.global.security;

public enum RoleEnum {
	SUPER_ADMIN,
	ADMIN,
	CONSULTANT_LEADER,
	CONSULTANT,
	STUDENT,
	VIEWER
	;
	
	/**
	 * spring security 형태로 바꾸기
	 * @return ROLE_ 추가
	 */
	public String getPrefix() {
		return "ROLE_"+this.name();
	}
}
