package com.ax.consultant;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ConsultantMapper {

	public List<ConsultantVO.Detail> selectList(ConsultantVO.Search consultantSearch);

	public int selectListTotalCount(ConsultantVO.Search consultantSearch);

	public ConsultantVO.Detail selectOne(int consultantNo);
	
	public int selectIsInCharge(
			@Param("consultantNo") int consultantNo,
			@Param("studentNos") Set<Integer> studentNos);

	public int insertCharged(
			@Param("consultantNo") int consultantNo,
			@Param("studentNos") Set<Integer> studentNos);
	
	public int deleteCharged(
			@Param("consultantNo") int consultantNo,
			@Param("studentNo") int studentNo);

}
