package com.ax.student;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {

	public void insertStudent(StudentVO.Register studentRegister);

	public List<StudentVO.Detail> selectStudentList(StudentVO.Search studentSearch);

	public int selectStudentListTotalCount(StudentVO.Search studentSearch);

	public StudentVO.Detail selectStudent(int studentNo);

	public StudentVO.Detail selectStudentForRecordApi(int studentNo);

	public List<StudentVO.Detail> selectCharged(int consultantNo);

}
