package com.axaboutconsulting.consultant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

@RestController
@RequestMapping("/consultant")
public class ConsultantApiController {
	@Autowired
	public ConsultantService service;
	
	@PostMapping("/list")
	public ResponseEntity<SearchResultVO> list(ConsultantVO.Search consultantSearch){
		
		return null;
	}

}
