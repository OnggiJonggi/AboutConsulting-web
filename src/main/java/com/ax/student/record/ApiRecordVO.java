package com.ax.student.record;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ApiRecordVO {
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@Builder
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class ApiRequest {
		private MultipartFile file;
		private String studentName;
		private String grade;
		private String semester;
		private String targets; // json형태를 텍스트로 받음
		private String majorTrack;
	}
	
	/**
	 * ApiRequest의 targets 변환용
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@Builder
	public static class Targets{
		private String school;
		private String major;
	}
	
	/**
	 * 생기부 api 응답 첫 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class ApiResponseWrapper{
	    private boolean success;
	    private String message;
	    private ApiDataBlock data;
	}
	
	/**
	 * 생기부 api 응답 두 번째 wrapper
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class ApiDataBlock{
	    private String analysisId; // 분석 결과 uuid
	    private String documentId; // 문서 uuid
	    private String createdAt; // 분석 생성 일시
	    private ApiResultsBlock results;
	}
	
	/**
	 * 생기부 응답
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class ApiResultsBlock {
	    private String gradeStrengthWeakness; // 성적 강점 및 약점 요약
	    private String gradeInDepthAnalysis; // 과목별 성적 심층 분석
	    private String lifeRecordStrengthWeakness; // 활동 기록 강정 및 약점
	    private String lifeRecordDiagnosisOverview; // 종합진단
	    private String academicRoadmap; // 성적 향상 로드맵
	    private String projectRecommendations; // 프로젝트 추천
	    private String careerBookRecommendations; // 도서 추천
	    private String parentStudentMessage; // 조언
	}
}
