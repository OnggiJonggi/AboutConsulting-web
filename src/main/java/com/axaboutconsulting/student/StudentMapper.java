package com.axaboutconsulting.student;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {

	public int insertStudent(StudentVO.Register studentRegister);

	public List<StudentVO.Detail> selectStudentList(StudentVO.Search studentSearch);

	public int selectStudentListTotalCount(StudentVO.Search studentSearch);

}
