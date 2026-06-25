package com.ax.consultant.org;

import java.util.List;
import java.util.Set;

import com.ax.consultant.ConsultantVO;
import com.ax.global.common.SearchPageVO;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class OrgVO {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@ToString
	public static class Insert{
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int orgNo;
		
		@NotBlank(message="이름을 입력해주세용")
		@Pattern(regexp=OrgRegexp.NAME_REGEXP, message="이름이 이상해용")
		private String name;
		
		@NotBlank(message="리더는 필수에용")
		private String encLeaderNo;
		private int leaderNo;
		
		// 소속될 컨설턴트
		@NotEmpty(message="리더라도 있어야 해용")
		private Set<String> encConsultantNos;
		private Set<Integer> consultantNos;
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class Detail{
		@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
		private int orgNo;
		private String encOrgNo; // orgNo 암호화
		
		private String name;
		private OrgStatusEnum status;
		private int consultantCount; // 소속 컨설턴트 수
		private int studentCount; // 전체 컨설턴트 담당 학생 총합
		
		// MEMBER 테이블
		private int leaderNo;
		private String encLeaderNo; // leaderNo 암호화
		private String leaderNickname;
		
		// 소속 컨설턴트 정보
		private List<ConsultantVO.Detail> consultantDetail;
		
		// 암호화, 복호화용
		public void setEncOrgNo(String encOrgNo) {
			this.encOrgNo = encOrgNo;
		}
		public void setLeaderNo(int leaderNo) {
			this.leaderNo = leaderNo;
		}
		public void setEncLeaderNo(String encLeaderNo) {
			this.encLeaderNo = encLeaderNo;
		}
		public void setOrgNo(int orgNo) {
			this.orgNo = orgNo;
		}
		public void setConsultantDetail(List<ConsultantVO.Detail> consultantDetail) {
			this.consultantDetail = consultantDetail;
		}
	}
	
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Search extends SearchPageVO{
		private String name;
		private OrgStatusEnum status;
		
		private String consultantName; // 소속 컨설턴트 이름
		private String consultantNickname; // 소속 컨설턴트 별명
	}
}
