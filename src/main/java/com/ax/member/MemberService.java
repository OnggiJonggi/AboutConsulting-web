package com.ax.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.member.MemberVO.SearchRequest;
import com.ax.member.MemberVO.SearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService{
	private final PasswordEncoder passwordEncoder;
	private final MemberMapper memberMapper;
    
	/**
	 * 새로운 계정 생성
	 */
	public void join(MemberVO.Join member) {
		//요 쿼리 세 개 못 합치나?
		
		// 아이디, 닉네임 중복 검사
		if(memberMapper.selectCheckId(member.getUserId()) > 0
				|| memberMapper.selectCheckNickname(member.getNickname()) > 0)
			throw new CustomException(ErrorCodeEnum.CANNOT_CREATE_MEMBER);
		
		// 비밀번호 암호화
		member.setUserPwd(passwordEncoder.encode(member.getUserPwd()));
		
		memberMapper.insertJoin(member);
	}

	/**
	 * 아이디 중복 확인
	 */
	public void checkId(String userId) {
		if(memberMapper.selectCheckId(userId) > 0)
			throw new CustomException(ErrorCodeEnum.ID_IS_DUPLICATED);
	}

	/**
	 * 닉네임 중복 확인
	 */
	public void checkNick(String nickname) {
		if(memberMapper.selectCheckNickname(nickname) > 0)
			throw new CustomException(ErrorCodeEnum.NICKNAME_IS_DUPLICATED);
	}

	public ResponseEntity<SearchResultVO<SearchResponse>> getList(SearchRequest search) {
		
		//권한 확인
		
		
		
		return null;
	}
    
    

}
