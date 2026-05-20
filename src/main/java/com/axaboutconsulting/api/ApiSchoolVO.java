package com.axaboutconsulting.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class ApiSchoolVO {
	/**
	 * api요청 첫 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class apiResponseWrapper {
		private List<apiSchoolInfoBlock> schoolInfo;
		
		public List<apiResponse> getData() {
			if (schoolInfo == null || schoolInfo.size() < 2)
				return List.of();
			return schoolInfo.get(1).getRow(); // 1번째 블록이 실제 데이터
		}
	}

	/**
	 * api요청 두 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class apiSchoolInfoBlock {
		private List<Object> head;
		private List<apiResponse> row;

		public List<apiResponse> getRow() {
			return row != null ? row : List.of();
		}
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@ToString
	public static class apiResponse {
		private String SD_SCHUL_CODE; // 학교행정코드
		private String SCHUL_NM; // 학교명
		private String LCTN_SC_NM; // 시도명
		private String ORG_RDNMA; // 도로롱주소
		private String COEDU_SC_NM; // 공학
		private String HS_SC_NM; // 고등학교구분명
		private String SPCLY_PURPS_HS_ORD_NM; // 특수고등학교계열명
	}
	/*
	 * api 응답 json : 
{
  "schoolInfo": [
    {
      "head": [
        {
          "list_total_count": 3
        },
        {
          "RESULT": {
            "CODE": "INFO-000",
            "MESSAGE": "정상 처리되었습니다."
          }
        }
      ]
    },
    {
      "row": [
        {
          "ATPT_OFCDC_SC_CODE": "F10",
          "ATPT_OFCDC_SC_NM": "광주광역시교육청",
          "SD_SCHUL_CODE": "7380148",
          "SCHUL_NM": "보문고등학교",
          "ENG_SCHUL_NM": "Bomun High School",
          "SCHUL_KND_SC_NM": "고등학교",
          "LCTN_SC_NM": "광주광역시",
          "JU_ORG_NM": "광주광역시교육청",
          "FOND_SC_NM": "사립",
          "ORG_RDNZC": "62397 ",
          "ORG_RDNMA": "광주광역시 광산구 어등대로529번길 37",
          "ORG_RDNDA": "(운수동)",
          "ORG_TELNO": "062-940-8800",
          "HMPG_ADRES": "bomun.gen.hs.kr",
          "COEDU_SC_NM": "남여공학",
          "ORG_FAXNO": "062-940-8899",
          "HS_SC_NM": "일반고",
          "INDST_SPECL_CCCCL_EXST_YN": "N",
          "HS_GNRL_BUSNS_SC_NM": "일반계",
          "SPCLY_PURPS_HS_ORD_NM": null,
          "ENE_BFE_SEHF_SC_NM": "전기",
          "DGHT_SC_NM": "주간",
          "FOND_YMD": "19741227",
          "FOAS_MEMRD": "19750301",
          "LOAD_DTM": "20260423"
        },
        {
          "ATPT_OFCDC_SC_CODE": "G10",
          "ATPT_OFCDC_SC_NM": "대전광역시교육청",
          "SD_SCHUL_CODE": "7430057",
          "SCHUL_NM": "보문고등학교",
          "ENG_SCHUL_NM": "Bomoon High School",
          "SCHUL_KND_SC_NM": "고등학교",
          "LCTN_SC_NM": "대전광역시",
          "JU_ORG_NM": "대전광역시교육청",
          "FOND_SC_NM": "사립",
          "ORG_RDNZC": "34619 ",
          "ORG_RDNMA": "대전광역시 동구 우암로 57",
          "ORG_RDNDA": ", 보문고등학교 (삼성동, 보문고등학교,보문중학교)",
          "ORG_TELNO": "042-620-6600",
          "HMPG_ADRES": "http://bomoonhs.djsch.kr",
          "COEDU_SC_NM": "남",
          "ORG_FAXNO": "042-672-9571",
          "HS_SC_NM": "일반고",
          "INDST_SPECL_CCCCL_EXST_YN": "N",
          "HS_GNRL_BUSNS_SC_NM": "일반계",
          "SPCLY_PURPS_HS_ORD_NM": null,
          "ENE_BFE_SEHF_SC_NM": "전기",
          "DGHT_SC_NM": "주간",
          "FOND_YMD": "19530221",
          "FOAS_MEMRD": "19530926",
          "LOAD_DTM": "20260423"
        },
        {
          "ATPT_OFCDC_SC_CODE": "G10",
          "ATPT_OFCDC_SC_NM": "대전광역시교육청",
          "SD_SCHUL_CODE": "7441034",
          "SCHUL_NM": "보문중학교",
          "ENG_SCHUL_NM": "Bomoon Middle School",
          "SCHUL_KND_SC_NM": "중학교",
          "LCTN_SC_NM": "대전광역시",
          "JU_ORG_NM": "대전광역시동부교육지원청",
          "FOND_SC_NM": "사립",
          "ORG_RDNZC": "34619 ",
          "ORG_RDNMA": "대전광역시 동구 우암로 57",
          "ORG_RDNDA": "(삼성동, 보문중학교)",
          "ORG_TELNO": "042-620-6705",
          "HMPG_ADRES": "http://bomoonms.djsch.kr",
          "COEDU_SC_NM": "남",
          "ORG_FAXNO": "042-672-9365",
          "HS_SC_NM": null,
          "INDST_SPECL_CCCCL_EXST_YN": "N",
          "HS_GNRL_BUSNS_SC_NM": "일반계",
          "SPCLY_PURPS_HS_ORD_NM": null,
          "ENE_BFE_SEHF_SC_NM": "전기",
          "DGHT_SC_NM": "주간",
          "FOND_YMD": "19451220",
          "FOAS_MEMRD": "19451220",
          "LOAD_DTM": "20260423"
        }
      ]
    }
  ]
}
	 */
}
