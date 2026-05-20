package com.axaboutconsulting.school;

import com.axaboutconsulting.api.ApiSchoolVO;
import com.axaboutconsulting.global.regex.SchoolRegexp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class SchoolVO {
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class Registor{
		
		@NotBlank(message="코드가 뭐에요")
		private String schoolCode;
		
		@NotBlank(message="이름이?")
		@Pattern(regexp = SchoolRegexp.NAME_REGEXP, message="이름이 이상해요")
		private String name;
		
		@NotBlank(message="어디니?")
		@Pattern(regexp = SchoolRegexp.PROVINCE_REGEXP, message="주소가 이상해요")
		private String sido;
		
		@Pattern(regexp = SchoolRegexp.PROVINCE_REGEXP, message="주소가 이상해요")
		private String sigungu;
		
		@Pattern(regexp = SchoolRegexp.COEDUCATION_REGEXP, message="공학 여부가 이상해요")
		private String coeducation;
		
		@Pattern(regexp = SchoolRegexp.SPECIALIZED_REGEXP, message="전문학교인가요?")
		private String specialized;
		
		@Pattern(regexp = SchoolRegexp.SPECIALIZED_TYPE_REGEXP, message="전문학교인가요?")
		private String specializedType;
		
		
	}
	
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@ToString
	@Builder
	public static class Detail{
		private String schoolCode;
		private String name;
		private String sido;
		private String sigungu;
		private String coeducation;
		private String specialized;
		private String specializedType;
		
		/**
		 * 공공데이터 api응답을
		 * Detail로 고쳐 쓰기
		 * 
		 * @param ApiSchoolVO.apiResponse
		 * @Return SchoolVO.Detail
		 */
		public static SchoolVO.Detail from(ApiSchoolVO.apiResponse response) {
			String coeducation;
			String sigungu = "";
			String specialized = "";
			String specializedType = "";
			
			// 주소
			if(response.getORG_RDNMA()!=null) {
				String[] addresses = response.getORG_RDNMA().split(" ");
				if (addresses.length > 1) {
					if (addresses[1].endsWith("시")
						|| addresses[1].endsWith("군")
						|| addresses[1].endsWith("구")) {
						sigungu = addresses[1];
					}
				}
			}
			
			// 남녀공학
		    if(response.getCOEDU_SC_NM() == null) {
		    	coeducation = "";
		    }else if(response.getCOEDU_SC_NM().equals("남여공학")) {
				coeducation = "공학";
			}else {
				coeducation = response.getCOEDU_SC_NM();
			}
			
			// 특성화고
		    if(response.getHS_SC_NM() != null) {
			    if(response.getHS_SC_NM().equals("자율고")
					|| response.getHS_SC_NM().equals("특성화고")
					|| response.getHS_SC_NM().equals("특목고")
					) {
					specialized = response.getHS_SC_NM();
				}
		    }
		    
		    // 특성화고 구분명
		    if(response.getSPCLY_PURPS_HS_ORD_NM()!=null
							&& !response.getSPCLY_PURPS_HS_ORD_NM().isEmpty()) {
		    	specializedType = response.getSPCLY_PURPS_HS_ORD_NM();
		    }
			
			return SchoolVO.Detail.builder()
					.schoolCode(response.getSD_SCHUL_CODE())
					.name(response.getSCHUL_NM())
					.sido(response.getLCTN_SC_NM())
					.sigungu(sigungu)
					.coeducation(coeducation)
					.specialized(specialized)
					.specializedType(specializedType)
					.build();
		}
	}
}
