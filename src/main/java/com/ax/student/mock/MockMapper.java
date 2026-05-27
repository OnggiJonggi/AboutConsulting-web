package com.ax.student.mock;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MockMapper {

	public List<MockVO.Detail> selectMockScoreList(int studentNo);

}
