package com.axaboutconsulting.member;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {
	private final MemberService memberService;

	/**
	 * 아이디 중복 확인
	 * @param member
	 * @return 정상 200, 이상해요 500
	 */
	@GetMapping("/check-id")
	public ResponseEntity<Void> checkId(String userId) {
		memberService.checkId(userId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 닉네임 중복 확인
	 * @param nickname
	 * @return 정상 200, 이상해요 500
	 */
	@GetMapping("/check-nickname")
	public ResponseEntity<Void> checkNickName(String nickname) {
		memberService.checkNick(nickname);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 회원 목록
	 * 관리자 권한
	 * @param search
	 * @return List<MemberVO.SearchResponse>
	 * @return count
	 */
	@GetMapping("/list")
	public ResponseEntity<SearchResultVO<MemberVO.SearchResponse>> getList(MemberVO.SearchRequest search){
		return memberService.getList(search);
	}
	
}
