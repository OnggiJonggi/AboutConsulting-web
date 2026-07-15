package legacy;

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

import com.ax.student.AnalysisTypeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * (미사용)
 * 원 위치 : student
 * 
 * GPT로 OCR된 생기부 분석, 모의고사 분석
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisComponent {
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	@Value("${google-studio.key}")
	private String keyStr;
	
	
	/**
	 * open api에 분석 요청
	 */
	public String run(String prompt, AnalysisTypeEnum type) {
		
		// 헤더
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+keyStr);
		headers.setContentType(MediaType.APPLICATION_JSON);
	    
		// 바디
		List<Map<String, String>> messages = List.of(Map.of("role", "system", "content", type.getSystemPrompt()),
				Map.of("role", "user", "content", prompt));

		Map<String, Object> body = new HashMap<>();
		body.put("model", "");
		body.put("messages", messages);
		body.put("temperature", 0.3);
		body.put("max_completion_tokens", 2048);
		body.put("response_format", Map.of("type", "json_object"));
		
		// 요청
	    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);		ResponseEntity<String> response = restTemplate.postForEntity(
				"https://api.openai.com/v1/chat/completions",
				requestEntity,
				String.class
				);
		
		
		// 응답 상태값
		HttpStatusCode statusCode = response.getStatusCode();
		if (!statusCode.is2xxSuccessful())
			throw new RuntimeException("OPEN API 호출 실패: HTTP " + statusCode.value());
		
		// 문자열로 바꾸기
		JsonNode root = objectMapper.readTree(response.getBody());
		return root.path("choices").get(0).path("message").path("content").asString();
	}
}
