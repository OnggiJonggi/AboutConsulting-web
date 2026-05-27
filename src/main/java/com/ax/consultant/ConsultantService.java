package com.ax.consultant;

import org.springframework.stereotype.Service;

import com.ax.global.common.SearchResultVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultantService{

	public SearchResultVO<ConsultantVO.Detail> getList(ConsultantVO.Search consultantSearch) {
		
		
		return null;
	}
	
}
