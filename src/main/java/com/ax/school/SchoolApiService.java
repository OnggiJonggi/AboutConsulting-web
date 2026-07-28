package com.ax.school;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolApiService {
	private final RestTemplate restTemplate;
//	private final HmacComponent hmacComponent;

	@Value("${neis-data.key}")
	private String niesKeyStr;
	
	@Value("${public-data.key}")
	private String publicKeyStr;

	/**
	 * 나이스 교육정보포털 초/중/고 학교 데이터
	 * https://open.neis.go.kr/portal/data/service/selectServicePage.do
	 * 
	 * 초중고 모두 검색하는 사소한 찐빠가 있는데
	 * 고등학교만 검색하고, 필요한 필드 제공하는 api가 없어요.
	 * 고등학교만 추출하려면 이름에서 '~고'형식만 찾아내는 방식이 있는데,
	 * 가끔 '고'로 끝나지 않는 고등학교가 있어요
	 * 아니면 suffix가 '~초', '~중'인 학교 때치때치해서 쓰던가
	 * 근데 나중에 중학교도 서비스 추가한다니까 대충 쓰죠 걍
	 */
	public List<SchoolVO.Detail> getSchool(String schoolName) throws Exception {

		// 학교명 UTF-8로 인코딩하고 url에 삽입
		String urlStr = "https://open.neis.go.kr/hub/schoolInfo"
				+ "?KEY=" + niesKeyStr
				+ "&Type=json&pIndex=1&pSize=5"
				+ "&SCHUL_NM=" + URLEncoder.encode(schoolName, StandardCharsets.UTF_8);
		
		// 인코딩된 문자열로 uri만들기
		// 인코딩 안 된 문자열을 인코딩해 만드려면 new URI()사용
		URI uri = URI.create(urlStr);
		
		// 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.ALL));
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

		// 요청
		ResponseEntity<SchoolApiVO.NiesWrapper1> response = 
				restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
				SchoolApiVO.NiesWrapper1.class);
		
		SchoolApiVO.NiesWrapper1 body = response.getBody();
		
		// 파싱, Detail로 옮기기
		List<SchoolVO.Detail> result = new ArrayList<>();
		if (body != null && body.getData() != null) {
			for (SchoolApiVO.NiesBlock school : body.getData()) {
				result.add(SchoolVO.Detail.from(school));
			}
		}
		
		return result;
	}

	/**
	 * (미사용)
	 * 공공데이터 전국대학별학과정보표준데이터
	 * 
	 * 대학교 검색도 고등학교 검색처럼 api로 구현하려고 했는데
	 * 대학교 + 그 대학교에 개설된 학과
	 * 이 두 가지를 동시에 추출하는 api가 없어요
	 * 이 api가 둘 다 조회는 되는데
	 * 검색어가 정확히 동일해야 조회되요(일부만 같아도 검색되는거 없음)
	 * 그래서 내다버림 쓸모없는넘
	 */
//	public List<SchoolVO.UnivDetail> getUniv(String univ, String major) throws Exception {
//
//		// 잘못된 파라미터
//		if(univ==null || univ.equals("") || major==null || major.equals(""))
//			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//		
//		
//		// 학교명 UTF-8로 인코딩하고 url에 삽입
//		String urlStr = "https://api.data.go.kr/openapi/tn_pubr_public_univ_major_api"
//				+ "?serviceKey=" + publicKeyStr
//				+ "&Type=json&pageNo=1&numOfRows=5"
//				+ "&SCHL_NM=" + URLEncoder.encode(univ, StandardCharsets.UTF_8)
//				+ "&SCHL_SE_NM=" + URLEncoder.encode("대학교", StandardCharsets.UTF_8)
//				+ "&SCSBJT_NM=" + URLEncoder.encode(major, StandardCharsets.UTF_8);
//		
//		// 인코딩된 문자열로 uri만들기
//		// 인코딩 안 된 문자열을 인코딩해 만드려면 new URI()사용
//		URI uri = URI.create(urlStr);
//		
//		// 헤더 생성
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(List.of(MediaType.ALL));
//		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
//
//		// 요청
//	    RestTemplate restTemplate = new RestTemplate();
//	    ResponseEntity<String> response = restTemplate.exchange(
//	            uri, HttpMethod.GET, httpEntity, String.class);
//
//	    if (response.getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_XML)) {
//	    	/* 
//	    	 * 오류가 발생하면 json이 아닌 xml방식으로 와요.
//	    	 * 그러니 xml을 파싱하도록 바꿔야 하는데
//	    	 * 귀찮으니까 mime가 XML이다 = 404로 둘게요
//	    	 */
//	        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//	    }
//	    
//	    
//	    
//	    
//	    // 파싱
//	    ObjectMapper mapper = new ObjectMapper();
//	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//	    SchoolApiVO.OpenDataWrapper apiResponse = mapper.readValue(
//	            response.getBody(), SchoolApiVO.OpenDataWrapper.class);
//
//	    // 폐과 제외
//	    List<SchoolVO.UnivDetail> result = apiResponse.getItems().stream()
//	            .filter(item -> !"폐과".equals(item.getMajorStatus()))
//	            .collect(Collectors.toList());
//	    
//	    // 대학+학과 HMAC해싱
//	    for(SchoolVO.UnivDetail univDetail : result) {
//	    	String univMajor = univDetail.getUniv()+univDetail.getMajor();
//	    	univDetail.setHmac(hmacComponent.hashing(univMajor));
//	    }
//	    
//	    return result;
//	}

}
