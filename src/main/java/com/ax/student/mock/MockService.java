package com.ax.student.mock;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockService {
	private final MockMapper mockMapper;

	/**
	 * 모의고사 목록 조회
	 * @param studentNo
	 * @return List<MockVO.Detail> 모의고사 리스트
	 */
	public List<MockVO.Detail> getMockScoreList(int studentNo) {
		return mockMapper.selectMockScoreList(studentNo);
	}
}
