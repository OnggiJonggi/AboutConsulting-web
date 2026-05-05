package com.axaboutconsulting.global.security;

import org.apache.ibatis.annotations.Mapper;

import com.axaboutconsulting.global.security.RoleVO.UrlAccessCheck;

@Mapper
public interface RoleMapper {

	public String selectRoleByMemberNo(int memberNo);

	public int selectUrlAccessCheck(UrlAccessCheck urlAccessCheck);
	
}
