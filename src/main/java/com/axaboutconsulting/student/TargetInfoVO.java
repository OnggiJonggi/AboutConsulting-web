package com.axaboutconsulting.student;

import java.util.ArrayList;
import java.util.List;

import com.axaboutconsulting.global.regex.StudentRegexp;

import jakarta.validation.Valid;
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
		@Valid
		private List<
			@Pattern(regexp = StudentRegexp.TARGET_MAJOR_REGEXP, message="전공이 이상해요")
			String> targetMajor;
		
		@Valid
		private List<
			@Pattern(regexp = StudentRegexp.TARGET_UNIV_REGEXP, message="대학 이름이 이상해요")
			String> targetUniv;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class Detail{
		private List<String> targetMajor;
		private List<String> targetUniv;
	}
	
	/**
	 * db 통신용 객체
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Update{
		private int studentNo;
		private List<Pair> targetPair;
		
		/**
		 * Register, Detail 형식을 DB조회 가능하게 바꿈
		 * @param register
		 * @param studentNo
		 */
		public Update(Register register, int studentNo) {
			List<String> targetMajor = register.targetMajor;
			List<String> targetUniv = register.targetUniv;
			this.studentNo = studentNo;
			
	        List<Pair> pairs = new ArrayList<>();
	        for(int i=0; i<targetMajor.size(); i++) {
	        	pairs.add(new Pair(targetMajor.get(i),targetUniv.get(i)));
	        }
	        this.targetPair = pairs;
		}
		public Update(Detail detail, int studentNo) {
			List<String> targetMajor = detail.targetMajor;
			List<String> targetUniv = detail.targetUniv;
			this.studentNo = studentNo;
			
			List<Pair> pairs = new ArrayList<>();
			for(int i=0; i<targetMajor.size(); i++) {
				
				if(targetMajor.get(i) == null || targetMajor.get(i).isEmpty()) break;
				
				pairs.add(new Pair(targetMajor.get(i),targetUniv.get(i)));
			}
			this.targetPair = pairs;
		}
	}
	
	/**
	 * Update에서 사용하는 객체
	 */
	@AllArgsConstructor
	@Getter
	@ToString
	public static class Pair{
		private String major;
		private String univ;
	}
}
