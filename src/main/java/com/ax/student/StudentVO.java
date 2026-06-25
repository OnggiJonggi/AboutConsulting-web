package com.ax.student;

import java.util.List;

import com.ax.global.common.SearchPageVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class StudentVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@ToString(callSuper = true)
	public static class Insert{
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int studentNo;
		
		@NotBlank(message="이름이 뭐에요")
		@Pattern(regexp = StudentRegexp.NAME_REGEXP, message="이름이 뭐이래")
		private String name;
		
		@Min(value = 1, message = "학년은 1 이상이어야 합니다")
		@Max(value = StudentRegexp.GRADE_MAX, message = "학년은 3 이하이어야 합니다")
		private Integer grade;
		
		@Min(value = 1, message = "학기는 1 이상이에요")
		@Max(value = StudentRegexp.SEMESTER_MAX, message = "학기는 2 이하에요")
		private Integer semester;
		
		@NotBlank(message="계열이 뭐에요")
		@Pattern(regexp = StudentRegexp.TRACK_REGEXP, message="계열이 왜이래")
		private String track;
		
		private int schoolCode;
		
		private List<TargetInfoVO.Insert> target;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	@Builder
	public static class Detail {
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int studentNo;
		private String encryptedStudentNo;
		
		private int schoolCode;
		private String name;
		private int grade;
		private int semester;
		private String track;
		private StudentStatusEnum status;
		
		// SCHOOL 테이블
		private String schoolName;
		
		// TARGET 테이블
		private List<TargetInfoVO.Pair> target;
		
		// CONSULTANT 테이블
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int consultantNo;
		private String encConsultantNo;
		private String consultantNickname;
		
		// CONSULTANT_ORG 테이블
		private String consultantOrgName;
		
		/**
		 * 식별번호 암호화용 setter
		 */
		public void setStudentNo(int studentNo) {
			this.studentNo = studentNo;
		}
		public void setEncryptedStudentNo(String encryptedStudentNo) {
			this.encryptedStudentNo = encryptedStudentNo;
		}
		public void setConsultantNo(int consultantNo) {
			this.consultantNo = consultantNo;
		}
		public void setEncConsultantNo(String encConsultantNo) {
			this.encConsultantNo = encConsultantNo;
		}
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Search extends SearchPageVO{
		private String name;
		private int grade;
		private int semester;
		private String track;
		
		// SCHOOL 테이블
		private String schoolName;
		
		// TARGET 테이블
		private String targetUniv;
		private String targetMajor;
		
		// CONSULTANT 테이블
		private Boolean isCharged; // 컨설턴트 배정 여부
		private String consultantNickname;
		
		// CONSULTANT_ORG 테이블
		private String consultantOrgName;
	}
}
