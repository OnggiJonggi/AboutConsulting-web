package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCode;
import com.axaboutconsulting.student.StudentVO.Add;

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
	public void addStudent(Add studentAdd) {
		if(studentMapper.insertStudent(studentAdd) == 0 )
			throw new CustomException(ErrorCode.CANNOT_ADD_STUDENT);
	}

}
