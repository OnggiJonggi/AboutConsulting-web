package com.axaboutconsulting.global.security;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.axaboutconsulting.global.security.RoleVO.UrlAccessCheck;

@Mapper
public interface RoleMapper {

	public List<String> selectMemberRole(int number);

	public int selectUrlAccessCheck(UrlAccessCheck urlAccessCheck);
	
}
