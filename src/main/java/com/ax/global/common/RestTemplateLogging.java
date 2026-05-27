package com.ax.global.common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * RestTemplate의 로그 인터셉터
 */
@Slf4j
public class RestTemplateLogging implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		log.info("=== HTTP 요청 ===");
		log.info("URI     : {}", request.getURI());
		log.info("Method  : {}", request.getMethod());
		log.info("Headers : {}", request.getHeaders());
		log.info("Body    :\n{}", sanitizeBody(body, request));

		ClientHttpResponse response = execution.execute(request, body);

		log.info("=== HTTP 응답 ===");
		log.info("Status  : {}", response.getStatusCode());
		log.info("Headers : {}", response.getHeaders());

		return response;
	}

	private String sanitizeBody(byte[] body, HttpRequest request) {
		String rawBody = new String(body, StandardCharsets.UTF_8);
		String contentType = String.valueOf(request.getHeaders().getContentType());

		if (!contentType.contains("multipart")) {
			return rawBody;
		}

		String[] lines = rawBody.split("\r?\n");
		StringBuilder result = new StringBuilder();
		boolean isBinaryPart = false;
		boolean isDataSection = false;
		boolean alreadyMasked = false;

		for (String line : lines) {
			// boundary 줄 → 출력 대신 줄바꿈으로 대체
			if (line.startsWith("--")) {
				isBinaryPart = false;
				isDataSection = false;
				alreadyMasked = false;
				result.append("\n"); // ← boundary 대신 빈 줄
				continue;
			}

			// 파일 파트 감지
			if (line.contains("Content-Disposition") && line.contains("filename=")) {
				isBinaryPart = true;
				result.append(line).append("\n");
				continue;
			}

			// 바이너리 파트 헤더 출력
			if (isBinaryPart && !isDataSection && !line.trim().isEmpty()) {
				result.append(line).append("\n");
				continue;
			}

			// 빈 줄 → 데이터 구간 시작 표시만
			if (isBinaryPart && line.trim().isEmpty()) {
				isDataSection = true;
				continue;
			}

			// 바이너리 데이터 → 마스킹 한 줄만
			if (isBinaryPart && isDataSection) {
				if (!alreadyMasked) {
					result.append("[바이너리 데이터 생략]\n");
					alreadyMasked = true;
				}
				continue;
			}

			// 일반 텍스트 파트
			if (!line.trim().isEmpty()) {
				result.append(line).append("\n");
			}
		}

		return result.toString();
	}
}
