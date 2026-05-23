package com.axaboutconsulting.global.async;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class AsyncVO {
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	@Builder
	public static class Insert{
		private int asyncNo;
		private int jobIdNo;
		private String type;
		private String status;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Select{
		private String type;
		private LocalDateTime startedAt;
		private LocalDateTime completedAt;
		private String status;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Builder
	public static class Update{
		private int asyncNo;
		private String status;
	}
}
