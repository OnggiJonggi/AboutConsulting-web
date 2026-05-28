package com.ax.student.record;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ax.global.common.RestTemplateLogging;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.student.StudentMapper;
import com.ax.student.StudentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiRecordService {
	private final StudentMapper studentMapper;
	private final RecordMapper recordMapper;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	@Value("${record-analysis}")
	private String url;
	
	/**
	 * 생기부 api요청 및 DB저장
	 * 생기부 원본 저장 로직 추가 필요해요
	 * 
	 * @param encryptedStudentNo
	 * @param filebytes
	 */
	@Async
	public void analysisRecord(int studentNo, int groupNo, byte[] filebytes) throws Exception{
		/*
		 * 생기부 원본 저장 로직 필요!
		 */
		
		try {
			// 학생 정보 조회
			StudentVO.Detail studentDetail = studentMapper.selectStudentForRecordApi(studentNo);
			
			// 지망 학교/학과 json화
			List<Map<String, String>> targetMapList = studentDetail.getTarget().stream()
				.map(p -> Map.of(
				    "school", p.getUniv(),
				    "major",  p.getMajor()
				))
				.collect(Collectors.toList());
			
			// 생기부 파일 정상화
		    ByteArrayResource fileResource = new ByteArrayResource(filebytes) {
		        @Override
		        public String getFilename() {
		            return "test.pdf";
		        }
		    };
		    
			// api요청
		    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		    body.add("file", fileResource);
		    body.add("student_name", studentDetail.getName());
		    body.add("grade", studentDetail.getGrade() + "학년");
		    body.add("semester", studentDetail.getSemester() + "학기");
		    body.add("targets", objectMapper.writeValueAsString(targetMapList));
		    
		    // major_track 정확한 형식 필요
//		    body.add("major_track", studentDetail.getTrack());
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	
		    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		    
		    
		    // -----로깅 인터셉터
			BufferingClientHttpRequestFactory factory =
					new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
			RestTemplate restTemplate = new RestTemplate(factory);
			restTemplate.setInterceptors(List.of(new RestTemplateLogging()));
			// 로깅 인터셉터-----

			
			// 요청
		    ResponseEntity<String> response = restTemplate.postForEntity(
		        url,
		        requestEntity,
		        String.class
		    );
		    
		    // 응답 데이터 맵핑
		    ApiRecordVO.ApiResponseWrapper result = objectMapper.readValue(
		        response.getBody(),
		        ApiRecordVO.ApiResponseWrapper.class
		    );
			
		    if(result == null || result.getData() == null
		    		|| result.getData().getResults() == null)
		    	throw new CustomException(ErrorCodeEnum.RECORD_API_NOT_WORKING);
		    
			// DB저장
		    recordMapper.insertRecord(RecordVO.Insert.builder()
		    		.groupNo(groupNo)
		    		.result(result.getData().getResults()).build());
		    
		    // 비동기 요청 작업 상태값 수정
		    recordMapper.updateRecordStatus(RecordVO.GroupStatus.builder()
		    		.groupNo(groupNo)
		    		.status(RecordStatusEnum.ACTIVE.name()).build());
		    
		} catch (Exception e) {
			
			e.printStackTrace();
			
			// 비동기 요청 작업 상태값 실패로 수정
			recordMapper.updateRecordStatus(RecordVO.GroupStatus.builder()
					.groupNo(groupNo)
					.status(RecordStatusEnum.FAILED.name()).build());
		}
	}

	
	
	
}
