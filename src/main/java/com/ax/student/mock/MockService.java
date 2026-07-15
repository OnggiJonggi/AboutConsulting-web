package com.ax.student.mock;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.file.FileDataVO;
import com.ax.global.file.FileInfoVO;
import com.ax.global.file.FileMapper;
import com.ax.global.file.component.FileComponent;
import com.ax.global.file.component.RootSavePathEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MockService {
	private final MockMapper mockMapper;
	private final FileMapper fileMapper;
	private final FileComponent fileComponent;
	private final MockAsyncComponent apiMockComponent;

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
	@Transactional
	public int insertMock(FileDataVO file, int studentNo, int memberNo) throws Exception {
		
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		MockVO.GroupStatus groupStatus = MockVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(MockStatusEnum.READY.name()).build();
		
		int result1 = mockMapper.insertMockGroup(groupStatus);
		if(result1==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		int groupNo = groupStatus.getGroupNo();
		if(groupNo==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 저장
		FileInfoVO.HandOver handOver = new FileInfoVO.HandOver(
				file, memberNo, RootSavePathEnum.MOCK_FILE);
		fileComponent.save(handOver,
				fileNo ->{
					FileInfoVO.InsertMapping insertMapping = FileInfoVO.InsertMapping.builder()
							.target(RootSavePathEnum.MOCK_FILE)
							.groupNo(groupNo)
							.fileNo(groupNo).build();
					fileMapper.insertMapping(insertMapping);
				}
			);
		
		// 커밋 후 비동기 작업
		TransactionSynchronizationManager.registerSynchronization(
			    new TransactionSynchronization() {
			        @Override
			        public void afterCommit() {
//			        	apiMockComponent.analysisMock(studentNo, groupNo, file.getBytes());
			        }
			    }
			);
		
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
