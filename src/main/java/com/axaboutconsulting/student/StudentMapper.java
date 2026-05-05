package com.axaboutconsulting.student;

import org.apache.ibatis.annotations.Mapper;

import com.axaboutconsulting.student.StudentVO.Add;

@Mapper
public interface StudentMapper {

	public int insertStudent(Add studentAdd);

}
