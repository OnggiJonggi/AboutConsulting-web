package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.common.SearchResultVO;
import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCode;
import com.axaboutconsulting.global.security.CryptoComponent;

@Service
public class StudentServiceImpl implements StudentService{

	private final CryptoComponent cryptoComponent;
	private final StudentMapper studentMapper;
	public StudentServiceImpl(StudentMapper studentMapper, CryptoComponent cryptoComponent) {
		this.studentMapper = studentMapper;
		this.cryptoComponent = cryptoComponent;
	}
	
	/**
	 * 학생 추가
	 */
	@Override
	public void register(StudentVO.Register studentRegister) {
		if(studentMapper.insertStudent(studentRegister) == 0 )
			throw new CustomException(ErrorCode.CANNOT_ADD_STUDENT);
	}

	@Override
	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search studentSearch) throws Exception {
		
		SearchResultVO<StudentVO.Detail> searchResult = new SearchResultVO<StudentVO.Detail>(
				studentMapper.selectStudentList(studentSearch)
				,studentMapper.selectStudentListTotalCount(studentSearch)
				,studentSearch.getPage()
				);
		
		cryptoComponent.encryptList(searchResult.getList());
		
		return searchResult;
	}


}
