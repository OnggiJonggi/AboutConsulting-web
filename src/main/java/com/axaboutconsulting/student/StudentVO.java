package com.axaboutconsulting.student;

import com.axaboutconsulting.global.common.SearchPageVO;
import com.axaboutconsulting.global.regex.StudentRegexp;
import com.axaboutconsulting.global.security.CryptedNumberVO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class StudentVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Register {
		private int studentNo;
		
		@NotBlank(message="이름이 뭐에요")
		@Pattern(regexp = StudentRegexp.NAME_REGEXP, message="이름이 뭐이래")
		private String name;
		
		@Min(value = 1, message = "학년은 1 이상이어야 합니다.")
		@Max(value = 3, message = "학년은 3 이하이어야 합니다.")
		private int grade;
		
		@NotBlank(message="계열이 뭐에요")
		@Pattern(regexp = StudentRegexp.TRACK_REGEXP, message="이과생이 아니라면 들어올 수 없다")
		private String track;
		
		@NotBlank(message="목표 전공이 뭐에요")
		@Pattern(regexp = StudentRegexp.TARGET_MAJOR_REGEXP, message="네놈의 그 하찮은 전공이 뭐지?")
		private String targetMajor;
		
		@NotBlank(message="목표 대학이 뭐에요")
		@Pattern(regexp = StudentRegexp.TARGET_UNIV_REGEXP, message="대학을 보여줘")
		private String targetUniv;
		
		private int highSchoolCode;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Search extends SearchPageVO{
		private String name;
		private String grade;
		private String track;
		private String targetMajor;
		private String targetUniv;
		
		private String schoolName;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Detail extends CryptedNumberVO{
		private String name;
		private int grade;
		private String track;
		private String targetMajor;
		private String targetUniv;
		
		private String schoolName;
	}
	
}
