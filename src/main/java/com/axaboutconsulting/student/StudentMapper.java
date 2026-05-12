package com.axaboutconsulting.student;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {

	public int insertStudent(StudentVO.Register studentRegister);

}
