package com.ax.consultant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
public class ConsultantApiController {
	private final ConsultantService consultantService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 컨설턴트 목록 조회
	 * 관리자
	 */
	@GetMapping("")
	public ResponseEntity<SearchResultVO<ConsultantVO.Detail>> getList(
			@RequestParam ConsultantVO.Search consultantSearch) throws Exception{
		
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(consultantSearch);
		
		// 없으면 404
		if(result==null || result.getList()==null || result.getList().isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 암호화
		for(ConsultantVO.Detail item : result.getList()) {
			item.setEncryptedConsultantNo(cryptoComponent.encrypt(String.valueOf(item.getConsultantNo())));
			item.setConsultantNo(0);
		}
		
		return ResponseEntity.ok(result);
	}
	
	

}
