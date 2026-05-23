package com.axaboutconsulting.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
	
	public MemberVO.Detail selectMemberById(String userId);

	public void insertJoin(MemberVO.Join member);

	public int selectCheckId(String userId);

	public int selectCheckNickname(String nickname);
}
