package com.axaboutconsulting.consultant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class ConsultantVO {
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Detail{
		private int consultantNo;
		private int memberNo;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Search{
		private String nickname;
		private String studentName;
	}
}
