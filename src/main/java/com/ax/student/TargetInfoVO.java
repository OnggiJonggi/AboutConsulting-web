package com.ax.student;

import com.ax.global.regex.StudentRegexp;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class TargetInfoVO {
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Register{
		@NotBlank(message="대학이 없어요")
		@Pattern(regexp = StudentRegexp.TARGET_UNIV_REGEXP, message="대학 이름이 이상해요")
		private String univ;
		
		@NotBlank(message="전공이 없어요")
		@Pattern(regexp = StudentRegexp.TARGET_MAJOR_REGEXP, message="전공이 이상해요")
		private String major;
		
		@Min(value = 1, message = "순번은 1에서 3까지")
		@Max(value = 3, message = "순번은 1에서 3까지")
		private int ranking;
	}
	
	@AllArgsConstructor
	@Getter
	@ToString
	public static class Pair{
		private String univ;
		private String major;
		private int ranking;
	}
}
