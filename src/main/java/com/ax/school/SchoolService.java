package com.ax.school;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SanitizeComponent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchoolService{
	private final SchoolMapper schoolMapper;
	private final SanitizeComponent sanitizeComponent;
	
	/**
	 * DB에서 학교 데이터 조회
	 */
	public List<SchoolVO.Detail> getList(String schoolName) {
		
		// 검색어 소독
		schoolName = sanitizeComponent.searchKeyword(schoolName, SchoolRegexp.NAME_MAX_LENGTH);
		
		return schoolMapper.selectSchoolList(schoolName);
	}

	/**
	 * 학교 추가
	 */
	public void insertOne(SchoolVO.Detail school) {
		int result = schoolMapper.insertSchool(school);
		if(result==0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}
	

}
