package com.axaboutconsulting.student;

import com.axaboutconsulting.global.regex.StudentRegexp;

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
	public static class Add {
		
		@NotBlank(message="이름이 뭐에요")
		@Pattern(regexp = StudentRegexp.NAME_REGEXP, message="이름이 뭐이래")
		private String name;
		
		@NotBlank(message="학년이 뭐에요")
		@Pattern(regexp = StudentRegexp.GRADE_REGEXP, message="너 대체 몇학년이야")
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
	}
}
