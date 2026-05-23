package com.axaboutconsulting.student;

public interface RecordService {

	RecordVO.Detail getRecord(String encryptedStudentNo) throws Exception;

}
