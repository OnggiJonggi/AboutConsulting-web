package com.ax.school;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolService{
	private final SchoolMapper schoolMapper;
	
	public List<SchoolVO.Detail> getList(String schoolName) {
		return schoolMapper.selectSchoolList(schoolName);
	}

	public void registor(SchoolVO.Detail target) {
		schoolMapper.insertSchool(target);
	}
	

}
