package com.axaboutconsulting.student;

import com.axaboutconsulting.global.common.SearchResultVO;

public interface StudentService {
	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search studentSearch)throws Exception ;
	
	public String register(StudentVO.Register studentRegister) throws Exception;
}
