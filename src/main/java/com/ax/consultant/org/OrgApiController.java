package com.ax.consultant.org;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
@Validated
public class OrgApiController {
	private final OrgService orgService;
	private final CryptoComponent cryptoComponent;

	
	/**
	 * 소속 검색
	 */
	@GetMapping("")
	public ResponseEntity<SearchResultVO<OrgVO.Detail>> getList(
			@ModelAttribute OrgVO.Search search) throws Exception{
		
		// 목록 조회
		SearchResultVO<OrgVO.Detail> result = orgService.getList(search);
		
		// 대표 컨설턴트 식별번호, 소속 식별번호 암호화
		if(result!= null && result.getList()!=null && !result.getList().isEmpty()) {
			for(OrgVO.Detail item : result.getList()) {
				item.setEncLeaderNo(cryptoComponent.encrypt(String.valueOf(item.getLeaderNo())));
				item.setEncOrgNo(cryptoComponent.encrypt(String.valueOf(item.getOrgNo())));
				item.setOrgNo(0);
				item.setLeaderNo(0);
			}
		}
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 소속 이름 중복확인
	 * 관리자, 대표 컨설턴트
	 */
	@GetMapping("check-name")
	public ResponseEntity<Void> checkName(
			@RequestParam(required=false) String encOrgNo,
			@RequestParam String name) throws Exception{
		
		/*
		 * encOrgNo가 없으면 소속 등록에 사용하는 중복확인
		 * 있으면 소속 이름 변경에 사용하는 중복확인
		 */
		int orgNo = 0;
		if(encOrgNo != null)
			orgNo = Integer.valueOf(cryptoComponent.decrypt(encOrgNo));
		
		orgService.checkName(orgNo, name);
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 이름 변경
	 * 관리자, 대표 컨설턴트
	 */
	@PutMapping("{encOrgNo}/name")
	public ResponseEntity<Void> updateName(
			@PathVariable String encOrgNo,
			@Pattern(regexp=OrgRegexp.NAME_REGEXP, message="이름이 이상해용")
			@RequestParam String name) throws Exception{
		
		int orgNo = Integer.valueOf(cryptoComponent.decrypt(encOrgNo));
		orgService.updateName(orgNo, name);
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 소속 상태값 변경
	 */
	@PutMapping("{encOrgNo}/status")
	public ResponseEntity<Void> updateStatus(
			@PathVariable String encOrgNo,
			@RequestParam OrgStatusEnum status) throws Exception{
		
		int orgNo = Integer.valueOf(cryptoComponent.decrypt(encOrgNo));
		orgService.updateStatus(orgNo, status);
		
		return ResponseEntity.ok().build();
	}
}
