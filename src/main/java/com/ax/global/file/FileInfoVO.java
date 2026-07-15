package com.ax.global.file;

import java.time.LocalDateTime;

import com.ax.global.file.component.FileStatusEnum;
import com.ax.global.file.component.RootSavePathEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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

		private String encFileNo;
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
		private RootSavePathEnum target;

		private int groupNo;
		private int fileNo;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	public static class Basic{
		private String originalName;
		private String changedName;
		private String mime;
		private String savePath;
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
		private String encGroupNo;

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
	
	// FileComponent.save() 전용 객체
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@SuperBuilder
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class HandOver extends FileDataVO{
		int memberNo;
		RootSavePathEnum rootSavePath;
		
		public HandOver(FileDataVO file, int memberNo, RootSavePathEnum rootSavePath) {
			super(file.getOriginalName(), file.getMime(), file.getSize(), file.getBytes());
			this.memberNo = memberNo;
			this.rootSavePath = rootSavePath;
		}
	}
}
