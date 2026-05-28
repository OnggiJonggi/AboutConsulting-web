package com.ax.student.mock;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ApiMockVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class ApiResponseWrapper{
	    private String year;
	    private String month;
	    private List<ApiMockVO.ApiResultsBlock> scores;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class ApiResultsBlock{
		private String subject;
		private String chosenSubject;
		private Integer normalScore;
		private Integer standardScore;
		private Integer percentile;
		private Integer grade;
	}
}
