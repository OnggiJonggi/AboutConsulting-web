package com.axaboutconsulting.student;

import com.axaboutconsulting.global.common.SearchResultVO;

public interface StudentService {

	public void register(StudentVO.Register studentRegister);

	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search studentSearch)throws Exception ;
}
