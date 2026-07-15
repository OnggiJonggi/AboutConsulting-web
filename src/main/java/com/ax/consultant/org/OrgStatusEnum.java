package com.ax.consultant.org;

/**
 * CONSULTANT_ORG 테이블 STATUS
 */
public enum OrgStatusEnum {
	ACTIVE, // 정상
	SUSPENDED, // 정지
	DELETED, // 삭제
	;
	
	private OrgStatusEnum() {};
}
