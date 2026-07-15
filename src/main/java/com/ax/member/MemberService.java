																												package com.ax.member;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SanitizeComponent;
import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService{
	private final PasswordEncoder passwordEncoder;
	private final MemberMapper memberMapper;
	private final CryptoComponent cryptoComponent;
	private final SanitizeComponent sanitizeComponent;
    
	/**
	 * 새로운 계정 생성
	 */
	public void join(MemberVO.Insert member) {
		
		// 아이디, 닉네임 중복 검사
		if(memberMapper.selectCheckId(member.getUserId()) > 0)
			throw new CustomException(ErrorCodeEnum.ID_IS_DUPLICATED);
		if(memberMapper.selectCheckNickname(member.getNickname()) > 0)
			throw new CustomException(ErrorCodeEnum.NICKNAME_IS_DUPLICATED);
		
		// 비밀번호 암호화
		member.setUserPwd(passwordEncoder.encode(member.getUserPwd()));
		
		int result = memberMapper.insertJoin(member);
		if(result==0)
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
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
		
		// 검색어 소독
		nickname = sanitizeComponent.searchKeywordNotLike(nickname, MemberRegexp.NAME_MAX_LENGTH);

		// 중복 확인
		if(memberMapper.selectCheckNickname(nickname) > 0)
			throw new CustomException(ErrorCodeEnum.NICKNAME_IS_DUPLICATED);
	}

	/**
	 * 회원 목록 검색
	 */
	public SearchResultVO<MemberVO.Detail> getList(MemberVO.Search search) throws Exception {
		
		// 검색어 소독
		search.setUserId(sanitizeComponent.searchKeyword(search.getUserId(), MemberRegexp.ID_MAX_LENGTH));
		search.setName(sanitizeComponent.searchKeyword(search.getName(), MemberRegexp.NAME_MAX_LENGTH));
		search.setNickname(sanitizeComponent.searchKeyword(search.getNickname(), MemberRegexp.NAME_MAX_LENGTH));
		search.setPhone(sanitizeComponent.searchKeyword(search.getPhone(), MemberRegexp.PHONE_MAX_LENGTH));
		
		// 목록 조회
		List<MemberVO.Detail> result = memberMapper.selectMemberList(search);
		
		// 검색 결과 수
		int totalCount = memberMapper.selectMemberListTotalCount(search);

		// SearchResultVO로 감싸기
		SearchResultVO<MemberVO.Detail> searchResult = new SearchResultVO<MemberVO.Detail>(
				result, totalCount, search.getPage());
		
		// 회원 식별번호 암호화
		for(MemberVO.Detail member : searchResult.getList()) {
			member.setEncMemberNo(cryptoComponent.encrypt(member.getMemberNo()));
			member.setMemberNo(0);
		}
		
		return searchResult;
	}

	/**
	 * 회원 기본 정보 조회
	 */
	public MemberVO.Detail getBasicInfo(int memberNo) {
		return memberMapper.selectMember(memberNo);
	}

	/**
	 * 회원 기본 정보 수정
	 */
	public void updateMemberBasicInfo(MemberVO.Update member) {
		
		// 별명 중복 확인
		if(memberMapper.selectUpdatedNickname(member.getMemberNo(), member.getNickname()) > 0)
			throw new CustomException(ErrorCodeEnum.NICKNAME_IS_DUPLICATED);
		
		// 비번 암호화
		if(member.getUserPwd()!=null
				&& !member.getUserPwd().isEmpty())
			member.setUserPwd(passwordEncoder.encode(member.getUserPwd()));

		int result = memberMapper.updateMember(member);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 회원 권한 수정
	 */
	@Transactional
	public void updateMemberRole(int memberNo, RoleEnum role) {
		
		// 권한 지워버려
		int deleteResult = memberMapper.deleteRole(memberNo);
		if(deleteResult==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 권한 생성하기
		int result = memberMapper.insertRole(memberNo, role);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 회원 상태 수정
	 */
	public void updateMemberStatus(int memberNo, MemberStatusEnum status) {
		int result = memberMapper.updateStatus(memberNo,status);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 닉네임 중복 확인 (수정용)
	 */
	public void checkUpdatedNickname(int memberNo, String nickname) {
		
		// 검색어 소독
		nickname = sanitizeComponent.searchKeywordNotLike(nickname, MemberRegexp.NAME_MAX_LENGTH);
		
		// 조회
		if(memberMapper.selectUpdatedNickname(memberNo, nickname) > 0)
			throw new CustomException(ErrorCodeEnum.NICKNAME_IS_DUPLICATED);
	}
    
    

}
