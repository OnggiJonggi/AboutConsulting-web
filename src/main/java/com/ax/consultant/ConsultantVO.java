package com.ax.consultant;

import com.ax.global.common.SearchPageVO;
import com.fasterxml.jackson.annotation.JsonProperty;

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
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int consultantNo;
		
		// studentNo 암호화 후 뷰 페이지에 대신 내보냄
		private String encryptedConsultantNo;
		
		
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Search extends SearchPageVO{
		private String nickname;
		private String studentName;
	}
}
