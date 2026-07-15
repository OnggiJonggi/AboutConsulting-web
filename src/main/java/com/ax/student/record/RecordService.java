package com.ax.student.record;

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
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordService{
	private final RecordMapper recordMapper;
	private final FileMapper fileMapper;
	private final FileComponent fileComponent;
	private final RecordAsyncComponent apiRecordComponent;
	
	/**
	 * 생기부 저장 및 분석
	 * 
	 * @param file
	 * @param studentNo
	 * @param memberNo
	 */
	@Transactional
	public int insertRecord(FileDataVO file, int studentNo, int memberNo) throws Exception {
		
		RecordVO.GroupStatus recordGroup = RecordVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(RecordStatusEnum.READY.name())
				.build();
		
		// 생기부 분석결과 묶음 + 비동기 요청 작업 상태값 생성
		int result = recordMapper.insertAnalysisGroup(recordGroup);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 묶음 식별번호 없으면 가세요
		int groupNo = recordGroup.getGroupNo();
		if(groupNo==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 저장
		FileInfoVO.HandOver handOver = new FileInfoVO.HandOver(
				file, memberNo, RootSavePathEnum.RECORD_FILE);
		fileComponent.save(handOver,
				fileNo ->{
					FileInfoVO.InsertMapping insertMapping = FileInfoVO.InsertMapping.builder()
							.target(RootSavePathEnum.RECORD_FILE)
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
						apiRecordComponent.analysisRecord(studentNo, groupNo, file.getBytes());
			        }
			    }
			);
		
		return groupNo;
	}

	/**
	 * 생기부 DB 조회
	 */
	public RecordVO.Detail getRecord(int studentNo) throws Exception {
		
		// DB조회
		RecordVO.Detail result = recordMapper.selectRecord(studentNo);
		if(result!=null) return result;
		
		// 진행 중인 생기부 비동기 작업이 있는지 확인
		if(recordMapper.selectRecordStatusByStudentNo(
				RecordVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(RecordStatusEnum.READY.name()).build()
				) > 0)
			result = RecordVO.Detail.builder().status(RecordStatusEnum.READY.name()).build();
		
		// 분석을 시도한 적이 없어요
		else result = RecordVO.Detail.builder().status(RecordStatusEnum.EMPTY.name()).build();
		
		return result;
	}

	/**
	 * groupNo로 생기부 상태 확인
	 * @param encStudentNo
	 */
	public String getStatus(int groupNo) throws Exception {
		return recordMapper.selectRecordStatus(groupNo);
	}

}
