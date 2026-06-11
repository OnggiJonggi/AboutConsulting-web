package com.ax.consultant;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConsultantMapper {

	public List<ConsultantVO.Detail> selectList(ConsultantVO.Search consultantSearch);

	public int selectListTotalCount(ConsultantVO.Search consultantSearch);

	public ConsultantVO.Detail selectOne(int consultantNo);

}
