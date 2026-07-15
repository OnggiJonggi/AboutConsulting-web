package com.ax.student.record;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM 분석 결과용 객체
 */
public class ApiRecordVO {

	/**
	 * GSW: Grade Strength & Weakness (성적 강점 및 약점 요약)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Gsw {
		@JsonProperty("grade_strengths")
		private List<String> gradeStrengths;

		@JsonProperty("grade_weaknesses")
		private List<String> gradeWeaknesses;

		public String toStorageString() {
			StringBuilder sb = new StringBuilder();
			if (gradeStrengths != null)
				for (String s : gradeStrengths)
					sb.append("[강점] ").append(s).append("\n");
			if (gradeWeaknesses != null)
				for (String w : gradeWeaknesses)
					sb.append("[약점] ").append(w).append("\n");
			return sb.toString().trim();
		}
	}

	/**
	 * GIDA: Grade In-Depth Analysis (과목별 성적 심층 분석)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Gida {
		@JsonProperty("grade_in_depth_analysis")
		private Detail gradeInDepthAnalysis;

		@AllArgsConstructor
		@NoArgsConstructor
		@Data
		public static class Detail {
			private String korean; // 국어
			private String math; // 수학
			private String english; // 영어
			private String social; // 사회
			private String science; // 과학

			@JsonProperty("korean_history")
			private String koreanHistory; // 한국사

			@JsonProperty("general_elective")
			private String generalElective; // 일반선택

			public String toStorageString() {
				StringBuilder sb = new StringBuilder();
				sb.append("국어: ").append(korean).append("\n");
				sb.append("수학: ").append(math).append("\n");
				sb.append("영어: ").append(english).append("\n");
				sb.append("사회: ").append(social).append("\n");
				sb.append("과학: ").append(science).append("\n");
				sb.append("한국사: ").append(koreanHistory).append("\n");
				sb.append("일반선택: ").append(generalElective);
				return sb.toString();
			}
		}

		public String toStorageString() {
			return gradeInDepthAnalysis.toStorageString();
		}
	}

	/**
	 * LRSW: Life Record Strength & Weakness (활동 기록 강점 및 약점)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Lrsw {
		@JsonProperty("strength_summary")
		private List<String> strengthSummary;

		@JsonProperty("weakness_summary")
		private List<String> weaknessSummary;

		public String toStorageString() {
			StringBuilder sb = new StringBuilder();
			if (strengthSummary != null)
				for (String s : strengthSummary)
					sb.append("[강점] ").append(s).append("\n");
			if (weaknessSummary != null)
				for (String w : weaknessSummary)
					sb.append("[약점] ").append(w).append("\n");
			return sb.toString().trim();
		}
	}

	/**
	 * LRDO: Life Record Diagnosis Overview (종합 진단)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Lrdo {
		@JsonProperty("life_record_diagnosis_overview")
		private Detail lifeRecordDiagnosisOverview;

		@AllArgsConstructor
		@NoArgsConstructor
		@Data
		public static class Detail {
			@JsonProperty("one_line_summary")
			private String oneLineSummary; // 한줄 요약

			private String body; // 본문 (5문장 진단)

			public String toStorageString() {
				return "[한줄요약] " + oneLineSummary + "\n[본문] " + body;
			}
		}

		public String toStorageString() {
			return lifeRecordDiagnosisOverview.toStorageString();
		}
	}

	/**
	 * LRRA: Life Record Roadmap Academic (성적 향상 로드맵)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Lrra {
		@JsonProperty("academic_roadmap")
		private Detail academicRoadmap;

		@AllArgsConstructor
		@NoArgsConstructor
		@Data
		public static class Detail {
			private String title; // 로드맵 제목
			private String description; // 로드맵 설명 (2문장)
			private List<String> boxes; // 실행 항목 3개

			public String toStorageString() {
				StringBuilder sb = new StringBuilder();
				sb.append(title).append("\n").append(description).append("\n");
				for (int i = 0; i < boxes.size(); i++) {
					sb.append(i + 1).append(". ").append(boxes.get(i)).append("\n");
				}
				return sb.toString().trim();
			}
		}

		public String toStorageString() {
			return academicRoadmap.toStorageString();
		}
	}

	/**
	 * PR: Project Recommendation (프로젝트 추천)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Pr {
		@JsonProperty("project_and_advanced_activity_recommendations")
		private List<Item> projectAndAdvancedActivityRecommendations;

		@AllArgsConstructor
		@NoArgsConstructor
		@Data
		public static class Item {
			@JsonProperty("project_number")
			private int projectNumber; // 프로젝트 순번

			private String title; // 프로젝트 제목
			private String content; // 프로젝트 상세 내용
		}

		public String toStorageString() {
			StringBuilder sb = new StringBuilder();
			for (Item item : projectAndAdvancedActivityRecommendations) {
				sb.append(item.getProjectNumber()).append(". 제목: ").append(item.getTitle()).append("\n");
				sb.append("내용: ").append(item.getContent()).append("\n\n");
			}
			return sb.toString().trim();
		}
	}

	/**
	 * BR: Book Recommendation (도서 추천)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Br {
		@JsonProperty("career_book_recommendations")
		private List<Item> careerBookRecommendations;

		@AllArgsConstructor
		@NoArgsConstructor
		@Data
		public static class Item {
			@JsonProperty("book_number")
			private int bookNumber; // 도서 순번

			private String title; // 도서 제목
			private String reason; // 추천 이유
		}

		public String toStorageString() {
			StringBuilder sb = new StringBuilder();
			for (Item item : careerBookRecommendations) {
				sb.append(item.getBookNumber()).append(". 제목: ").append(item.getTitle()).append("\n");
				sb.append("이유: ").append(item.getReason()).append("\n\n");
			}
			return sb.toString().trim();
		}
	}

	/**
	 * PSM: Parent & Student Message (조언)
	 */
	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Psm {
		@JsonProperty("advice_for_parents")
		private String adviceForParents; // 부모에게 전하는 조언

		@JsonProperty("encouragement_message_for_student")
		private String encouragementMessageForStudent; // 학생에게 전하는 격려 메시지

		public String toStorageString() {
			return "[부모님께] " + adviceForParents + "\n[학생에게] " + encouragementMessageForStudent;
		}
	}
}
