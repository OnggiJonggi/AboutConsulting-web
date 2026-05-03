package com.axaboutconsulting.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
	
	public MemberVO.Detail selectMemberById(String userId);

	public int insertJoin(MemberVO.Join member);

	public int selectCheckId(String userId);
}
