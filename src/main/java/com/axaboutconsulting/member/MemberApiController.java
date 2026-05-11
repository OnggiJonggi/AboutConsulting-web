package com.axaboutconsulting.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

@RestController
@RequestMapping("/api/member")
public class MemberApiController {

	@Autowired
	private MemberService service;
	
	/**
	 * 아이디 중복 확인
	 * @param member
	 * @return 정상 200, 이상해요 500
	 */
	@PostMapping("/check-id")
	public ResponseEntity<Void> checkId(String userId) {
		service.checkId(userId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 닉네임 중복 확인
	 * @param nickname
	 * @return 정상 200, 이상해요 500
	 */
	@PostMapping("/check-nickname")
	public ResponseEntity<Void> checkNickName(String nickname) {
		service.checkNick(nickname);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 회원 목록
	 * 관리자 권한
	 * @param search
	 * @return List<MemberVO.SearchResponse>
	 * @return count
	 */
	@PostMapping("/list")
	public ResponseEntity<SearchResultVO<MemberVO.SearchResponse>> getList(MemberVO.SearchRequest search){
		return service.getList(search);
	}
}
