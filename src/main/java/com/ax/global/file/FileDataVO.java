package com.ax.global.file;

import lombok.Builder;
import lombok.Getter;

/**
 * Muiltipartfile 객체 변환용
 */
@Getter
@Builder
public class FileDataVO {
	private String originalName;
	private String mime;
	private long size;
	private byte[] bytes;
}
