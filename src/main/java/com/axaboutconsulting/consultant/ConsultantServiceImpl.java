package com.axaboutconsulting.consultant;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.global.common.SearchResultVO;

@Service
public class ConsultantServiceImpl implements ConsultantService{

	@Override
	public SearchResultVO<ConsultantVO.Detail> getList(ConsultantVO.Search consultantSearch) {
		
		
		return null;
	}
	
}
