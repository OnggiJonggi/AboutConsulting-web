package com.axaboutconsulting.school;

import java.util.List;

import org.springframework.stereotype.Service;

import com.axaboutconsulting.school.SchoolMapper;
import com.axaboutconsulting.school.SchoolVO;

@Service
public class SchoolServiceImpl implements SchoolService{
	private SchoolMapper schoolMapper;
	public SchoolServiceImpl(SchoolMapper schoolMapper) {
		this.schoolMapper = schoolMapper;
	}
	
	@Override
	public List<SchoolVO.Detail> search(String schoolName) {
		return schoolMapper.selectSchoolList(schoolName);
	}

	@Override
	public void registor(SchoolVO.Detail target) {
		schoolMapper.insertSchool(target);
	}
	

}
