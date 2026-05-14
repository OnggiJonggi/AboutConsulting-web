package com.axaboutconsulting.consultant;

import com.axaboutconsulting.global.common.SearchResultVO;

public interface ConsultantService {

	public SearchResultVO<ConsultantVO.Detail> getList(ConsultantVO.Search consultantSearch);
	
}
