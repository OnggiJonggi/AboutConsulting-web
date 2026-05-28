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

	/**
	 * 모의고사 묶음 생성
	 * @param studentNo
	 * @return 모의고사 묶음 번호
	 */
	public int createMockGroup(int studentNo) {
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		MockVO.GroupStatus groupStatus = MockVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(MockStatusEnum.READY.name()).build();
		
		mockMapper.insertMockGroup(groupStatus);
		
		return groupStatus.getGroupNo();
	}

	/**
	 * 모의고사 묶음 상태값 조회
	 * @param groupNo
	 * @return 모의고사 묶음 상태값
	 */
	public String getStatus(int groupNo) {
		return mockMapper.selectMockStatus(groupNo);
	}
}
