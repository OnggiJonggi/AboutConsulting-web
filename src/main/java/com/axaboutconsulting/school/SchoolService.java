package com.axaboutconsulting.school;

import java.util.List;

import com.axaboutconsulting.school.SchoolVO;

public interface SchoolService {

	public List<SchoolVO.Detail> search(String schoolName);

	public void registor(SchoolVO.Detail target);

}
