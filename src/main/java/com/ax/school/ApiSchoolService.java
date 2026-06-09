package com.ax.school;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.security.HmacComponent;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiSchoolService {
	private final RestTemplate restTemplate;
	private final HmacComponent hmacComponent;

	@Value("${nies-data.key}")
	private String niesKeyStr;
	
	@Value("${public-data.key}")
	private String publicKeyStr;

	/**
	 * 나이스 교육정보포털 초/중/고 학교 데이터
	 */
	public List<SchoolVO.Detail> getSchool(String schoolName) throws Exception {

		// 학교명 UTF-8로 인코딩하고 url에 삽입
		String urlStr = "https://open.neis.go.kr/hub/schoolInfo"
				+ "?KEY=" + niesKeyStr
				+ "&Type=json&pIndex=1&pSize=5"
				+ "&SCHUL_NM=" + URLEncoder.encode(schoolName, StandardCharsets.UTF_8);
		URI uri = new URI(urlStr);
		
		// 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.ALL));
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

		// 요청
		ResponseEntity<ApiSchoolVO.NiesWrapper1> response = 
				restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
				ApiSchoolVO.NiesWrapper1.class);
		
		ApiSchoolVO.NiesWrapper1 body = response.getBody();
		
		// 파싱, Detail로 옮기기
		List<SchoolVO.Detail> result = new ArrayList<>();
		if (body != null && body.getData() != null) {
			for (ApiSchoolVO.NiesBlock school : body.getData()) {
				result.add(SchoolVO.Detail.from(school));
			}
		}
		
		return result;
	}

	/**
	 * 공공데이터 전국대학별학과정보표준데이터
	 */
	public List<SchoolVO.UnivDetail> getUniv(String univ, String major) throws Exception {

		// 잘못된 파라미터
		if(univ==null || univ.equals("") || major==null || major.equals(""))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		
		// 학교명 UTF-8로 인코딩하고 url에 삽입
		String urlStr = "https://api.data.go.kr/openapi/tn_pubr_public_univ_major_api"
				+ "?KEY=" + publicKeyStr
				+ "&Type=json&pageNo=1&numOfRows=5"
				+ "&SCHL_NM=" + URLEncoder.encode(univ, StandardCharsets.UTF_8)
				+ "&SCHL_SE_NM=" + URLEncoder.encode("대학교", StandardCharsets.UTF_8)
				+ "&SCSBJT_NM=" + URLEncoder.encode(major, StandardCharsets.UTF_8);
		
		URI uri = new URI(urlStr);
		
		// 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.ALL));
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

		// 요청
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.exchange(
	            uri, HttpMethod.GET, httpEntity, String.class);

	    // 파싱
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	    ApiSchoolVO.OpenDataWrapper apiResponse = mapper.readValue(
	            response.getBody(), ApiSchoolVO.OpenDataWrapper.class);

	    // 폐과 제외
	    List<SchoolVO.UnivDetail> result = apiResponse.getItems().stream()
	            .filter(item -> !"폐과".equals(item.getMajorStatus()))
	            .collect(Collectors.toList());
	    
	    // 대학+학과 HMAC해싱
	    for(SchoolVO.UnivDetail univDetail : result) {
	    	String univMajor = univDetail.getUniv()+univDetail.getMajor();
	    	univDetail.setHmac(hmacComponent.hashing(univMajor));
	    }
	    
	    return result;
	}

}
