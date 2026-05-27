package com.ax.student.record;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService{
	private final RecordMapper recordMapper;

	/**
	 * RECORD_ANALYSIS_GROUP 테이블 행 생성 및 식별번호 반납 
	 * @param studentNo
	 * @return groupNo
	 */
	public int createAnalysisGroup(int studentNo) {
		// 생기부 분석결과 묶음 + 비동기 요청 작업 상태값 생성
		RecordVO.GroupStatus recordGroup = RecordVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(RecordStatusEnum.READY.name())
				.build();
		recordMapper.insertAnalysisGroup(recordGroup);

		return recordGroup.getGroupNo();
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
