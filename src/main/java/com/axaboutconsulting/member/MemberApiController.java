package com.axaboutconsulting.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

@RestController
@RequestMapping("/member/api")
public class MemberApiController {

	@Autowired
	private MemberService service;
	
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
