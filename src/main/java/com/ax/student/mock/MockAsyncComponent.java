package com.ax.student.mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class MockAsyncComponent {
	private final MockMapper mockMapper;
	
//	/**
//	 * 모의고사 분석 api요청
//	 * 
//	 * @param studentNo
//	 * @param groupNo
//	 * @param filebytes
//	 */
//	@Async
//	public void analysisMock(int studentNo, int groupNo, byte[] filebytes) {
//		try {
//			
//		    if(result == null || result.getScores() == null)
//		    	throw new CustomException(ErrorCodeEnum.MOCK_API_NOT_WORKING);
//			
//			// DB저장
//		    mockMapper.insertMock(MockVO.Insert.builder()
//		    	    .groupNo(groupNo)
//		    	    .result(result.getScores()).build());
//		    
//			// 비동기 요청 작업 상태값 성공으로 수정
//			mockMapper.updateMockStatus(MockVO.GroupStatus.builder()
//					.groupNo(groupNo)
//					.yearMonth(String.format("%d-%02d", result.getYear(), result.getMonth()))
//					.status(MockStatusEnum.ACTIVE.name()).build());
//			
//		} catch (Exception e) {
//			// 비동기 요청 작업 상태값 실패로 수정
//			mockMapper.updateMockStatus(MockVO.GroupStatus.builder()
//					.groupNo(groupNo)
//					.status(MockStatusEnum.FAILED.name()).build());
//		}
//
//	}
	
}
