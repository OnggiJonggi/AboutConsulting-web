package com.ax.consultant;

import java.util.List;

import com.ax.global.common.SearchPageVO;
import com.ax.member.MemberStatusEnum;
import com.ax.student.StudentVO;
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
		private int consultantNo; // 컨설턴트 식별번호(회원 식별번호)
		private String encryptedConsultantNo;
		
		// CONSULTANT_ORG 테이블
		private String orgName; // 소속 이름
		
		// CONSULTANT_STUDENT 테이블
		private int studentCount; // 담당 학생 수
		
		// STUDENT 테이블
		private List<StudentVO.Detail> charged; // 담당 학생

		
		
		// 암호화용 setter
		public void setConsultantNo(int consultantNo) {
			this.consultantNo = consultantNo;
		}
		public void setEncryptedConsultantNo(String encryptedConsultantNo) {
			this.encryptedConsultantNo = encryptedConsultantNo;
		}
		// 담당 학생 setter
		public void setCharged(List<StudentVO.Detail> charged) {
			this.charged = charged;
		}
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Search extends SearchPageVO{
		
		// MEMBER 테이블
		private String name; // 컨설턴트 이름
		private String nickname; // 컨설턴트 별명
		private MemberStatusEnum status; // 회원 상태
		
		// STUDENT 테이블
		private String studentName; // 담당 학생 이름
		
		// CONSULTANT_ORG 테이블
		private Boolean hasOrg; // 소속 있나요
		private String orgName; // 소속 이름
		
		// CONSULTANT_STUDENT 테이블
		private Boolean inCharged; // 담당 학생 있나요
		private int chargedCountStart; // 담당 학생 수 시작 번호
		private int chargedCountEnd; // 담당 학생 수 끝 번호
	}
}
