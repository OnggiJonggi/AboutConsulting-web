package com.axaboutconsulting.consultant;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.common.SearchResultVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultantService{

	public SearchResultVO<ConsultantVO.Detail> getList(ConsultantVO.Search consultantSearch) {
		
		
		return null;
	}
	
}
