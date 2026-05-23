package com.axaboutconsulting.student;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.axaboutconsulting.global.async.AsyncMapper;
import com.axaboutconsulting.global.async.AsyncStatusEnum;
import com.axaboutconsulting.global.async.AsyncTypeEnum;
import com.axaboutconsulting.global.async.AsyncVO;
import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCodeEnum;
import com.axaboutconsulting.global.security.CryptoComponent;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ApiRecordService {
	private final StudentMapper studentMapper;
	private final AsyncMapper asyncMapper;
	private final RecordMapper recordMapper;
	private final CryptoComponent cryptoComponent;

	/**
	 * 생기부 api요청 및 DB저장
	 * 생기부 원본 저장 로직 추가 필요해요
	 * 
	 * @param encryptedStudentNo
	 * @param filebytes
	 */
	@Async
	public void uploadRecord(String encryptedStudentNo, byte[] filebytes) throws Exception{
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// 비동기 요청 작업 상태값 추가
		AsyncVO.Insert asyncInsert = AsyncVO.Insert.builder()
				.jobIdNo(studentNo)
				.type(AsyncTypeEnum.RECORD.name())
				.status(AsyncStatusEnum.PROCESSING.name()).build();
		asyncMapper.insertStatus(asyncInsert);
		System.out.println(asyncInsert);
		
		try {
			// 학생 정보 조회
			StudentVO.Detail studentDetail = studentMapper.selectStudentForRecordApi(studentNo);
			System.out.println(studentDetail);
			
			// 지망 학교/학과 json화
			List<Map<String, String>> targetMapList = studentDetail.getTarget().stream()
				.map(p -> Map.of(
				    "school", p.getUniv(),
				    "major",  p.getMajor()
				))
				.collect(Collectors.toList());
			ObjectMapper objectMapper = new ObjectMapper();
			
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
		    body.add("grade", studentDetail.getGrade());
		    body.add("semester", studentDetail.getSemester());
		    body.add("targets", objectMapper.writeValueAsString(targetMapList));
		    body.add("major_track", studentDetail.getTrack());
		    
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	
		    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
	
		    RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<String> response = restTemplate.postForEntity(
		        "http://56.228.25.27/api/v1/analyze/",
		        requestEntity,
		        String.class
		    );
		    
		    System.out.println(response);
		    // 응답 데이터 맵핑
		    ApiRecordVO.ApiResponseWrapper result = objectMapper.readValue(
		        response.getBody(),
		        ApiRecordVO.ApiResponseWrapper.class
		    );
			
		    if(result == null || result.getData() == null
		    		|| result.getData().getResults() == null)
		    	throw new CustomException(ErrorCodeEnum.RECORD_API_NOT_WORKING);
		    
			// DB저장
		    recordMapper.insertRecord(new RecordVO
		    		.Insert(studentNo, result.getData().getResults()));
		    
		} catch (Exception e) {
			e.printStackTrace();
			// 비동기 요청 작업 상태값 실패로 수정
			asyncMapper.updateStatus(AsyncVO.Update.builder()
					.asyncNo(asyncInsert.getAsyncNo())
					.status(AsyncStatusEnum.FAILED.name()).build()
					);
			return;
		}
	    
		// 비동기 요청 작업 상태값 수정
		asyncMapper.updateStatus(AsyncVO.Update.builder()
				.asyncNo(asyncInsert.getAsyncNo())
				.status(AsyncStatusEnum.COMPLETED.name()).build()
				);
	}
	
	
	
}
