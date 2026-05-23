package com.axaboutconsulting.school;

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

@Service
@RequiredArgsConstructor
public class ApiSchoolService {
	private final RestTemplate restTemplate;

	@Value("${nies-data.key}")
	private String keyStr;

	public List<SchoolVO.Detail> search(String schoolName) throws Exception {

		// 학교명 UTF-8로 인코딩하고 url에 삽입
		String urlStr = "https://open.neis.go.kr/hub/schoolInfo"
				+ "?KEY=" + keyStr
				+ "&Type=json&pIndex=1&pSize=5"
				+ "&SCHUL_NM=" + URLEncoder.encode(schoolName, StandardCharsets.UTF_8);
		URI uri = new URI(urlStr);
		
		// 헤더 생성
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.ALL));
		HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

		// 요청
		ResponseEntity<ApiSchoolVO.ApiResponseWrapper> response = 
				restTemplate.exchange(uri, HttpMethod.GET, httpEntity,
				ApiSchoolVO.ApiResponseWrapper.class);
		
		ApiSchoolVO.ApiResponseWrapper body = response.getBody();
		
		// 파싱, Detail로 옮기기
		List<SchoolVO.Detail> result = new ArrayList<>();
		if (body != null && body.getData() != null) {
			for (ApiSchoolVO.ApiRowBlock school : body.getData()) {
				result.add(SchoolVO.Detail.from(school));
			}
		}
		
		return result;
	}

}
