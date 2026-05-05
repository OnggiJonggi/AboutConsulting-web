package com.axaboutconsulting.member;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.common.SearchResultVO;
import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCode;
import com.axaboutconsulting.member.MemberVO.SearchRequest;
import com.axaboutconsulting.member.MemberVO.SearchResponse;

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
	public void checkId(String userId) {
		if(memberMapper.selectCheckId(userId) > 0)
			throw new CustomException(ErrorCode.ID_IS_DUPLICATED);
	}

	@Override
	public void checkNick(String nickname) {
		if(memberMapper.selectCheckNickname(nickname) > 0)
			throw new CustomException(ErrorCode.NICKNAME_IS_DUPLICATED);
	}

	@Override
	public ResponseEntity<SearchResultVO<SearchResponse>> getList(SearchRequest search) {
		
		//권한 확인
		
		
		
		return null;
	}
    
    

}
