package com.ax.school;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class SchoolApiVO {
	/**
	 * nies api요청 첫 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NiesWrapper1 {
		private List<NiesWrapper2> schoolInfo;
		
		public List<NiesBlock> getData() {
			if (schoolInfo == null || schoolInfo.size() < 2)
				return List.of();
			return schoolInfo.get(1).getRow(); // 1번째 블록이 실제 데이터
		}
	}

	/**
	 * nies api요청 두 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class NiesWrapper2 {
		private List<Object> head;
		private List<NiesBlock> row;

		public List<NiesBlock> getRow() {
			return row != null ? row : List.of();
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class NiesBlock {
		@JsonProperty("SD_SCHUL_CODE")
		private String sdSchulCode;         // 학교행정코드
		
		@JsonProperty("SCHUL_NM")
		private String schulNm;             // 학교명
		
		@JsonProperty("LCTN_SC_NM")
		private String lctnScNm;            // 시도명
		
		@JsonProperty("ORG_RDNMA")
		private String orgRdnma;            // 도로명주소
		
		@JsonProperty("COEDU_SC_NM")
		private String coeduScNm;           // 공학여부
		
		@JsonProperty("HS_SC_NM")
		private String hsScNm;              // 고등학교구분명
		
		@JsonProperty("SPCLY_PURPS_HS_ORD_NM")
		private String spclyPurpsHsOrdNm;   // 특수고등학교계열명
	}
	
	
	/**
	 * 공공데이터 전국대학별학과정보표준데이터 전용
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class OpenDataWrapper{
		private Body body;
		
		public static class Body{
			@JsonProperty("body")
			Inner inner;
		}
		
		public static class Inner{
            @JsonProperty("items")
            List<SchoolVO.UnivDetail> items;
		}
		
        public List<SchoolVO.UnivDetail> getItems() {
            if (body == null || body.inner == null || body.inner.items == null)
                return Collections.emptyList();
            return body.inner.items;
        }
	}
}
