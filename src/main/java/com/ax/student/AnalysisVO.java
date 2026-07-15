package com.ax.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * LLM 응답용
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class AnalysisVO {
	private String interactionId;
	private String message;
}
