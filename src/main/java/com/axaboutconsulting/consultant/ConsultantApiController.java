package com.axaboutconsulting.consultant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

@RestController
@RequestMapping("/consultant")
public class ConsultantApiController {
	public final ConsultantService service;
	public ConsultantApiController(ConsultantService service) {
		this.service = service;
	}

	/**
	 * 컨설턴트 목록 조회
	 * @param consultantSearch
	 * @return 200, SearchResultVO
	 */
	@PostMapping("/list")
	public ResponseEntity<SearchResultVO<ConsultantVO.Detail>> getList(ConsultantVO.Search consultantSearch){
		return ResponseEntity.ok(service.getList(consultantSearch));
	}

}
