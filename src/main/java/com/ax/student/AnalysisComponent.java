package com.ax.student;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisComponent {
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	@Value("${google-aistudio.key}")
	private String keyStr;
	@Value("${google-aistudio.aimodel}")
	private String aiModel;
	
	
	/**
	 * 생기부 OCR + 분석
	 * 모델 : Gemini 3.1 pro
	 * 
	 * 제미니가 한국어 OCR도 잘 하면서 분석에도 적절함
	 * 이전 코드는 네이버 클로바 OCR + GPT를 사용했는데
	 * OCR이 자꾸 표를 인식 못 해서 OCR과 LLM 하나로 합침
	 * 
	 * @param studentPrompt : 학생 프롬프트
	 * @param type : 분석 대상
	 * @param filebytes : pdf파일
	 * @param interactionId : 생기부 재호출 id
	 * @return : AnalysisVO
	 */
	public AnalysisVO run(String studentPrompt, AnalysisTypeEnum type, byte[] filebytes, String interactionId) {
		
		// 헤더
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-goog-api-key", keyStr);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// body
		Map<String, Object> systemText = new HashMap<>();
		systemText.put("type", "text");
		systemText.put("text", type.getSystemPrompt());

		Map<String, Object> userText = new HashMap<>();
		userText.put("type", "text");
		userText.put("text", studentPrompt);

		List<Map<String, Object>> inputList = new ArrayList<>();
		inputList.add(systemText);
		inputList.add(userText);

		// 파일 있으면 같이 보내기
		if (filebytes != null) {
			Map<String, Object> imageContent = new HashMap<>();
			imageContent.put("type", "document");
			imageContent.put("data", Base64.getEncoder().encodeToString(filebytes));
			imageContent.put("mime_type", "application/pdf");
			inputList.add(imageContent);
		}

		Map<String, Object> body = new HashMap<>();
		body.put("model", aiModel);
		body.put("input", inputList);

		if (interactionId != null && !interactionId.isEmpty()) {
			body.put("previous_interaction_id", interactionId);
		}

		// json형식으로 주세요
		if (type.getResponseFormat() != null) {
			try {
				@SuppressWarnings("unchecked")
				Map<String, Object> schemaMap = objectMapper.readValue(type.getResponseFormat(), Map.class);
				Map<String, Object> responseFormat = new HashMap<>();
				responseFormat.put("type", "text");
				responseFormat.put("mime_type", "application/json");
				responseFormat.put("schema", schemaMap);
				body.put("response_format", responseFormat);
			} catch (Exception e) {
				log.error("응답 스키마 파싱 실패: {}", type.name(), e);
				throw new RuntimeException(e);
			}
		}

		// 요청
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				"https://generativelanguage.googleapis.com/v1beta/interactions",
				requestEntity,
				String.class);
		
		// 응답 상태값
		HttpStatusCode statusCode = response.getStatusCode();
		if (!statusCode.is2xxSuccessful())
			throw new RuntimeException("GEMINI 호출 실패: HTTP " + statusCode.value());
		
		// 문자열로 바꾸기
		JsonNode root = objectMapper.readTree(response.getBody());

		String newInteractionId = root.path("id").asString();

		String message = "";
		for (JsonNode step : root.path("steps")) {
			if ("model_output".equals(step.path("type").asString())) {
				message = step.path("content").get(0).path("text").asString();
				break;
			}
		}

		return AnalysisVO.builder().message(message).interactionId(newInteractionId).build();
	}
}
