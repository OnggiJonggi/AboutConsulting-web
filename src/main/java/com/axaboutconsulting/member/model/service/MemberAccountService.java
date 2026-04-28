package com.axaboutconsulting.member.model.service;

import com.axaboutconsulting.member.model.vo.Member;

import jakarta.validation.Valid;

public interface MemberAccountService {

	public void newAccount(@Valid Member.NewAccount member);

}
