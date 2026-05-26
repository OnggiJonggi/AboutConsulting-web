package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.security.CryptoComponent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService{
	private final RecordMapper recordMapper;
	private final CryptoComponent cryptoComponent;

	/**
	 * 생기부 DB 조회
	 */
	public RecordVO.Detail getRecord(String encryptedStudentNo) throws Exception {
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// DB조회
		RecordVO.Detail result = recordMapper.selectReord(studentNo);
		if(result!=null) return result;
		
		// 진행 중인 생기부 비동기 작업이 있는지 확인
		if(recordMapper.selectRecordStatus(
				RecordVO.GroupStatus.builder()
				.studentNo(studentNo)
				.status(RecordStatusEnum.READY.name()).build()
				) > 0)
			result = RecordVO.Detail.builder().status(RecordStatusEnum.READY.name()).build();
		
		// 분석을 시도한 적이 없어요
		else result = RecordVO.Detail.builder().status(RecordStatusEnum.EMPTY.name()).build();
		
		return result;
	}

	public int isActive(String encryptedStudentNo) throws Exception {
		return recordMapper.selectRecordStatus(
				RecordVO.GroupStatus.builder()
				.studentNo(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)))
				.status(RecordStatusEnum.ACTIVE.name()).build());
	}
}
