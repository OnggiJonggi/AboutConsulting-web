package com.ax.student.mock;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class MockVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Builder
	@ToString
	public static class GroupStatus{
		private int groupNo;
		private int studentNo;
		private String yearMonth; // YYYY-MM
		private String status;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Detail{
		private int mockNo;
		private String encryptedMockNo;
		private String yearMonth;
		private LocalDateTime startedAt;
		private LocalDateTime completedAt;
		private List<SubjectScore> subject;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	public static class SubjectScore{
		private String subject;
		private String chosenSubject;
		private Integer normalScore;
		private Integer standardScore;
		private Integer percentile;
		private int grade;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@Builder
	public static class Insert{
		private int groupNo;
		private List<ApiMockVO.ApiResultsBlock> result;
	}
}
