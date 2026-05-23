package com.axaboutconsulting.student;

import java.time.LocalDateTime;

import com.axaboutconsulting.student.ApiRecordVO.ApiResultsBlock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RecordVO {
	
//	@AllArgsConstructor
//	@NoArgsConstructor
//	@Data
//	public static class Original{
//		
//	}
	
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	@Builder
	public static class Insert{
		private int studentNo;
		private ApiResultsBlock result;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class Detail{
		private LocalDateTime createAt;
		private ApiResultsBlock result;
	}
}
