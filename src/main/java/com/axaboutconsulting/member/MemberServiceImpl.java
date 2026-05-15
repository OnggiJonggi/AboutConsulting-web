package com.axaboutconsulting.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.common.SearchResultVO;
import com.axaboutconsulting.global.exception.CustomException;
import com.axaboutconsulting.global.exception.ErrorCode;
import com.axaboutconsulting.member.MemberVO.SearchRequest;
import com.axaboutconsulting.member.MemberVO.SearchResponse;

@Service
public class MemberServiceImpl implements MemberService{
	
	private final PasswordEncoder passwordEncoder;
	private final MemberMapper memberMapper;
    public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }
    
	/**
	 * 새로운 계정 생성
	 */
	@Override
	public void join(MemberVO.Join member) {
		// 아이디 중복 검사
		
		// 닉네임 중복 검사
		
		// 비밀번호 암호화
		member.setUserPwd(passwordEncoder.encode(member.getUserPwd()));
		
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

	/**
	 * 닉네임 중복 확인
	 */
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
