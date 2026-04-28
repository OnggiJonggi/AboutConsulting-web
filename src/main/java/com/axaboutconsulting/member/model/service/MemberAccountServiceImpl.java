package com.axaboutconsulting.member.model.service;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.common.exception.CustomException;
import com.axaboutconsulting.common.exception.ErrorCode;
import com.axaboutconsulting.member.model.mapper.MemberMapper;
import com.axaboutconsulting.member.model.vo.Member;

@Service
public class MemberAccountServiceImpl implements MemberAccountService{
	
	private final MemberMapper memberMapper;
    public MemberAccountServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }
    
	/**
	 * 새로운 계정 생성
	 */
	@Override
	public void newAccount(Member.NewAccount member) {
		if(memberMapper.insertNewAccount(member)==0)
			throw new CustomException(ErrorCode.CANNOT_CREATE_MEMBER);
	}
    
    

}
