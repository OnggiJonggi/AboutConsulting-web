package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.async.AsyncMapper;
import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCodeEnum;
import com.axaboutconsulting.global.security.CryptoComponent;

@Service
public class RecordServiceImpl implements RecordService{
	private final RecordMapper recordMapper;
	private final AsyncMapper AsyncMapper;
	private final CryptoComponent cryptoComponent;
	public RecordServiceImpl(RecordMapper recordMapper, com.axaboutconsulting.global.async.AsyncMapper asyncMapper,
			CryptoComponent cryptoComponent) {
		this.recordMapper = recordMapper;
		AsyncMapper = asyncMapper;
		this.cryptoComponent = cryptoComponent;
	}

	/**
	 * 생기부 DB 조회
	 */
	@Override
	public RecordVO.Detail getRecord(String encryptedStudentNo) throws Exception {
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// DB조회
		RecordVO.Detail result = recordMapper.selectReord(studentNo);
		if(result!=null) return result;
		
		// 진행 중인 생기부 비동기 작업이 있는지 확인
		if(AsyncMapper.selectIsRecordProcessing(studentNo) > 0)
			throw new CustomException(ErrorCodeEnum.RECORD_ANALYSIS_IS_DUPLICATED);
		
		// 작업이 진행된 적이 없어요
		throw new CustomException(ErrorCodeEnum.RECORD_IS_EMPTY);
	}
}
