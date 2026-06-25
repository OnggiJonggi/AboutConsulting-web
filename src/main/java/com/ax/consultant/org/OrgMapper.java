package com.ax.consultant.org;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ax.consultant.ConsultantVO;

@Mapper
public interface OrgMapper {

	public List<OrgVO.Detail> selectList(OrgVO.Search search);

	public int selectListTotalCount(OrgVO.Search search);

	public int selectIsBelog(int memberNo);

	public OrgVO.Detail select(int orgNo);
	
	public List<ConsultantVO.Detail> selectConsultant(int orgNo);

	public int selectCheckName(
			@Param("orgNo") int orgNo,
			@Param("name") String name);

	public void insertOrg(OrgVO.Insert insert);

	public int updateConsultantOrg(
			@Param("orgNo") int orgNo,
			@Param("list") Set<Integer> consultantNos);

	public int updateName(
			@Param("orgNo") int orgNo,
			@Param("name") String name);

	public int updateStatus(
			@Param("orgNo") int orgNo,
			@Param("status") OrgStatusEnum status);

	public int selectIsSameOrg(
			@Param("no1") int no1,
			@Param("no2") int no2);
}
