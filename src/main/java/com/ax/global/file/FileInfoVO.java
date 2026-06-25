package com.ax.global.file;

import java.time.LocalDateTime;

import org.springframework.core.io.Resource;

import com.ax.global.file.component.FileStatusEnum;
import com.ax.global.file.component.TargetEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class FileInfoVO {

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@Builder
	@ToString
	public static class Insert {
		private int fileNo;
		private String originalName;
		private String changedName;
		private String mime;
		private long fileSize;
		private String savePath;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@Builder
	@ToString
	public static class Detail {

		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int fileNo;

		private String encryptedFileNo;
		private String originalName;
		private long fileSize;
		private LocalDateTime savedAt;

	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	@Getter
	@ToString
	public static class InsertMapping {
		private TargetEnum target;

		private int groupNo;
		private int fileNo;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	public static class GetFile {
		private String originalName;
		private String mime;
		private String savePath;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Builder
	@ToString
	public static class FileResult {
		private Resource resource;
		private String originalName;
		private String mimeType;
		private boolean inline; // true = 새 탭 렌더링, false = 다운로드
	}

	/**
	 * 파일 상태값 업테이트 전용
	 */
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Builder
	@ToString
	public static class UpdateStatus {
		private int GroupNo;
		private String encryptedGroupNo;

		private int fileNo;
		private String status;
	}

	// FILE_HISTORY 삽입
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@Builder
	@ToString
	public static class InsertHistory {
		private int fileNo;
		private int historyNo;
		private String originalName;
		private String changedName;
		private String savePath;
		private FileStatusEnum action;
		private LocalDateTime actionAt;
		private int actionBy;
	}

}
