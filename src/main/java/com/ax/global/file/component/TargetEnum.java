package com.ax.global.file.component;

import lombok.Getter;

/**
 * 파일의 맵핑 테이블 분류
 */
@Getter
public enum TargetEnum {
	RECORD_FILE("record"), // 생기부
	MOCK_FILE("mock"), // 모의고사
	;
	
	
	
	
	private final String saveFolder;
	
	private TargetEnum(String saveFolder) {
		this.saveFolder = saveFolder;
	}
}
