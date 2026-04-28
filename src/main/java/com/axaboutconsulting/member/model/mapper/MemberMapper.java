package com.axaboutconsulting.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.axaboutconsulting.member.model.vo.Member;

import jakarta.validation.Valid;

@Mapper
public interface MemberMapper {
	
	public Member.Detail selectMemberById(String userId);

	public int insertNewAccount(@Valid Member.NewAccount member);
}
