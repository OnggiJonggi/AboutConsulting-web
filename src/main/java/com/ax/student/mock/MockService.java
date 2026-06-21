package com.ax.student.mock;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.file.FileDataVO;
import com.ax.global.file.component.FileComponent;
import com.ax.global.file.component.TargetEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockService {
	private final MockMapper mockMapper;
	private final FileComponent fileComponent;
	private final ApiMockComponent apiMockComponent;

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
	public int insertMock(FileDataVO file, int studentNo, int memberNo) throws Exception {
		
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		MockVO.GroupStatus groupStatus = MockVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(MockStatusEnum.READY.name()).build();
		
		int result1 = mockMapper.insertMockGroup(groupStatus);
		if(result1==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		int groupNo = groupStatus.getGroupNo();
		if(groupNo==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 파일 저장
		fileComponent.saveFile(file, groupNo, TargetEnum.MOCK_FILE, memberNo);
		
		// 비동기 작업
		apiMockComponent.analysisMock(studentNo, groupNo, file.getBytes());
		
		return groupNo;
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
