package com.axaboutconsulting.member;

import org.springframework.http.ResponseEntity;

import com.axaboutconsulting.global.common.SearchResultVO;
import com.axaboutconsulting.member.MemberVO.SearchRequest;
import com.axaboutconsulting.member.MemberVO.SearchResponse;

public interface MemberService {

	public void join(MemberVO.Join member);

	public void checkId(String userId);

	public void checkNick(String nickname);

	public ResponseEntity<SearchResultVO<SearchResponse>> getList(SearchRequest search);

}
