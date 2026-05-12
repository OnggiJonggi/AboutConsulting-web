package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCode;

@Service
public class StudentServiceImpl implements StudentService{

	private final StudentMapper studentMapper;
	public StudentServiceImpl(StudentMapper studentMapper) {
		this.studentMapper = studentMapper;
	}
	
	/**
	 * 학생 추가
	 */
	@Override
	public void addStudent(StudentVO.Register studentRegister) {
		if(studentMapper.insertStudent(studentRegister) == 0 )
			throw new CustomException(ErrorCode.CANNOT_ADD_STUDENT);
	}

}
