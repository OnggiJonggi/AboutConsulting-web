package com.axaboutconsulting.student;

import com.axaboutconsulting.global.common.SearchResultVO;

public interface StudentService {
	public SearchResultVO<StudentVO.SearchResult> getList(StudentVO.Search studentSearch)throws Exception ;
	
	public String register(StudentVO.Register studentRegister) throws Exception;

	public StudentVO.Detail getStudentBasicInfo(String encryptedStudentNo) throws Exception;
}
