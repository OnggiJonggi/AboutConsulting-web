package com.ax.school;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchoolMapper {

	public List<SchoolVO.Detail> selectSchoolList(String schoolName);

	public void insertSchool(SchoolVO.Detail target);

}
