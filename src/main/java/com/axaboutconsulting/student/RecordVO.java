package com.axaboutconsulting.student;

import java.time.LocalDateTime;
import java.util.List;

import com.axaboutconsulting.student.ApiRecordVO.ApiResultsBlock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class RecordVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Builder
	@ToString
	public static class GroupStatus{
		private int groupNo;
		private int studentNo;
		private String status;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Builder
	@ToString
	public static class Detail{
		private List<AnalysisPair> analysisPair;
		private LocalDateTime startedAt;
		private LocalDateTime completedAt;
		private String status; // RecordStatusEnum참조
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class AnalysisPair{
		private String field;
		private String analysis;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@Builder
	public static class Insert{
		private int groupNo;
		private ApiResultsBlock result;
	}
}
