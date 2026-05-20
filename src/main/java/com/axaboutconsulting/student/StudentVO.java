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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
public class StudentVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@ToString(callSuper = true)
	public static class Register{
		private int studentNo;
		
		@NotBlank(message="이름이 뭐에요")
		@Pattern(regexp = StudentRegexp.NAME_REGEXP, message="이름이 뭐이래")
		private String name;
		
		@Min(value = 1, message = "학년은 1 이상이어야 합니다.")
		@Max(value = 3, message = "학년은 3 이하이어야 합니다.")
		private Integer grade;
		
		@NotBlank(message="계열이 뭐에요")
		@Pattern(regexp = StudentRegexp.TRACK_REGEXP, message="계열이 왜이래")
		private String track;

		private int schoolCode;
		
		private TargetInfoVO.Register target;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Search extends SearchPageVO{
		private String name;
		private String grade;
		private String track;
		
		private String schoolName;
		
		private TargetInfoVO.Detail target;
		
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Detail extends CryptedNumberVO{
		private String name;
		private int grade;
		private String track;
		
		private String schoolName;
		
		private TargetInfoVO.Detail target;
		
	}
	
}
