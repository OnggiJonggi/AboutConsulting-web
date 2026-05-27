package com.ax.student;

import java.util.List;

import com.ax.global.common.SearchPageVO;
import com.ax.global.regex.StudentRegexp;
import com.ax.global.security.CryptedNumberVO;

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
	public static class Register{
		private int studentNo;
		
		@NotBlank(message="이름이 뭐에요")
		@Pattern(regexp = StudentRegexp.NAME_REGEXP, message="이름이 뭐이래")
		private String name;
		
		@Min(value = 1, message = "학년은 1 이상이어야 합니다")
		@Max(value = 3, message = "학년은 3 이하이어야 합니다")
		private Integer grade;
		
		@Min(value = 1, message = "학기는 1 이상이에요")
		@Max(value = 2, message = "학년은 2 이하에요")
		private Integer semester;
		
		@NotBlank(message="계열이 뭐에요")
		@Pattern(regexp = StudentRegexp.TRACK_REGEXP, message="계열이 왜이래")
		private String track;
		
		private int schoolCode;
		
		private List<TargetInfoVO.Register> target;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	@Builder
	public static class Detail {
		private String name;
		private int grade;
		private int semester;
		private String track;
		
		private String schoolName;
		private List<TargetInfoVO.Pair> target;
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
		
		private String schoolName;
		
		private List<TargetInfoVO.Pair> target;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class SearchResult extends CryptedNumberVO{
		private Detail detail;
	}
}
