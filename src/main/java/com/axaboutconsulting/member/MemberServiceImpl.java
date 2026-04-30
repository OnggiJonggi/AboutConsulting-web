package com.axaboutconsulting.member;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.common.exception.CustomException;
import com.axaboutconsulting.common.exception.ErrorCode;
import com.axaboutconsulting.member.MemberVO.Join;

@Service
public class MemberServiceImpl implements MemberService{
	
	private final MemberMapper memberMapper;
    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }
    
	/**
	 * 새로운 계정 생성
	 */
	@Override
	public void join(MemberVO.Join member) {
		if(memberMapper.insertJoin(member)==0)
			throw new CustomException(ErrorCode.CANNOT_CREATE_MEMBER);
	}

	/**
	 * 아이디 중복 확인
	 */
	@Override
	public void checkId(Join member) {
		if(memberMapper.selectCheckId(member)==0) return;
		else throw new CustomException(ErrorCode.ID_IS_DUPLICATED);
	}
    
    

}
