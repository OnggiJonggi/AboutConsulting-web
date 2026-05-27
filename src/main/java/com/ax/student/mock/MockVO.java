package com.ax.student.mock;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class MockVO {
	
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
}
