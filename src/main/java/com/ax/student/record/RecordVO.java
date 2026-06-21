package com.ax.student.record;

import java.time.LocalDateTime;
import java.util.List;

import com.ax.student.record.ApiRecordVO.ApiResultsBlock;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int groupNo;
		
		private ApiResultsBlock result;
	}
}
