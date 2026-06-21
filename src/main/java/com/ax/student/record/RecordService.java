package com.ax.student.record;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.file.FileDataVO;
import com.ax.global.file.component.FileComponent;
import com.ax.global.file.component.TargetEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService{
	private final RecordMapper recordMapper;
	private final FileComponent fileComponent;
	private final apiRecordComponent apiRecordComponent;
	
	/**
	 * 생기부 저장 및 분석
	 * 
	 * @param file
	 * @param studentNo
	 * @param memberNo
	 */
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
		
		// 파일 저장
		fileComponent.saveFile(file, groupNo, TargetEnum.RECORD_FILE, memberNo);
		
		// 비동기 작업
		apiRecordComponent.analysisRecord(file.getBytes(), groupNo, studentNo);
		
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
	 * @param encryptedStudentNo
	 */
	public String getStatus(int groupNo) throws Exception {
		return recordMapper.selectRecordStatus(groupNo);
	}

}
