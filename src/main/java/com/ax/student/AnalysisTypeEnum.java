package com.ax.student;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import lombok.Getter;

@Getter
public enum AnalysisTypeEnum {
    GRADE_STRENGTH_WEAKNESS("prompt/grade/grade_strength_weakness"),
    GRADE_IN_DEPTH_ANALYSIS("prompt/grade/grade_in_depth_analysis"),
    LIFE_RECORD_STRENGTH_WEAKNESS("prompt/life_record/strength_weakness_summary"),
    LIFE_RECORD_DIAGNOSIS_OVERVIEW("prompt/life_record/diagnosis_overview"),
    LIFE_RECORD_ROADMAP_ACADEMIC("prompt/life_record/roadmap_academic"),
    PROJECT_RECOMMENDATION("prompt/recommendation/project_recommendation"),
    BOOK_RECOMMENDATION("prompt/recommendation/book_recommendation"),
    PARENT_STUDENT_MESSAGE("prompt/message/parent_student_message"),
//    MOCK("gpt-4.1-mini", null) //TODO:모의고사 프롬프트 어디갓어
    ;

	private final String systemPrompt;
	private final String responseFormat;

	AnalysisTypeEnum(String promptPath) {
		this.systemPrompt = loadPrompt(promptPath);
		this.responseFormat = loadResponseFormat(promptPath);
	}

	/**
	 * resources/prompt에서 프롬프트 파일 얻어오기
	 */
	private static String loadPrompt(String path) {
		path += ".txt";
		
		try (InputStream is = AnalysisTypeEnum.class.getClassLoader().getResourceAsStream(path)) {

			if (is == null) return null;
			
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);

		} catch (IOException e) {
			throw new RuntimeException("프롬프트 로딩 실패: " + path, e);
		}
	}
	
	/**
	 * resources/prompt에서 프롬프트 파일 얻어오기
	 */
	private static String loadResponseFormat(String path) {
		path += "_json.txt";
		
		try (InputStream is = AnalysisTypeEnum.class.getClassLoader().getResourceAsStream(path)) {
			
			if (is == null) return null;
			
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
			
		} catch (IOException e) {
			throw new RuntimeException("프롬프트 로딩 실패: " + path, e);
		}
	}
}
