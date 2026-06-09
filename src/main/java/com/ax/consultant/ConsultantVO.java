package com.ax.consultant;

import com.ax.global.common.SearchPageVO;
import com.ax.member.MemberStatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ConsultantVO {
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	public static class Detail{
		
		// MEMBER 테이블
		private String userId;
		private String name;
		private String nickname;
		private MemberStatusEnum status;
		
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int consultantNo;
		private String encryptedConsultantNo;
		
		private String orgName; // 소속 이름
		
		private int studentCount; // 담당 학생 수

		
		
		// 암호화용 setter
		public void setConsultantNo(int consultantNo) {
			this.consultantNo = consultantNo;
		}
		public void setEncryptedConsultantNo(String encryptedConsultantNo) {
			this.encryptedConsultantNo = encryptedConsultantNo;
		}
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Search extends SearchPageVO{
		
		private String name; // 컨설턴트 이름
		private String nickname; // 컨설턴트 별명
		
		private String studentName; // 담당 학생 이름
		
		private String orgName; // 소속 이름
	}
}
