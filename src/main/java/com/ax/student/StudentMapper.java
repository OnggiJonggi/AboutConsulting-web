package com.ax.student;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper {

	public void insertStudent(StudentVO.Insert studentRegister);

	public List<StudentVO.Detail> selectStudentList(StudentVO.Search studentSearch);

	public int selectStudentListTotalCount(StudentVO.Search studentSearch);

	public StudentVO.Detail selectStudent(int studentNo);

	public StudentVO.Detail selectStudentForRecordApi(int studentNo);

	public List<StudentVO.Detail> selectCharged(int consultantNo);

	public int updateStudent(StudentVO.Insert student);

	public int updateStatus(
			@Param("studentNo") int studentNo,
			@Param("status") StudentStatusEnum status);

}
