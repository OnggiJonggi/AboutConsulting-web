package com.ax.student.mock;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MockMapper {

	public List<MockVO.Detail> selectMockScoreList(int studentNo);

	public void insertMockGroup(MockVO.GroupStatus groupStatus);

	public void updateMockStatus(MockVO.GroupStatus groupStatus);

	public void insertMock(MockVO.Insert mockInsert);

	public String selectMockStatus(int groupNo);

}
