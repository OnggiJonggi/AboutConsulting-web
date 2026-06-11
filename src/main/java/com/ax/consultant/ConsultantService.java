package com.ax.consultant;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ax.global.common.SearchResultVO;
import com.ax.student.StudentMapper;
import com.ax.student.StudentVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultantService{
	private final ConsultantMapper consultantMapper;
	private final StudentMapper studentMapper;

	/**
	 * 컨설턴트 목록 조회
	 */
	public SearchResultVO<ConsultantVO.Detail> getList(ConsultantVO.Search consultantSearch) {
		
		// 조회
		List<ConsultantVO.Detail> list = consultantMapper.selectList(consultantSearch);
		
		// 검색 결과 수 조회
		int totalCount = consultantMapper.selectListTotalCount(consultantSearch);
		
		// 결과 객체 만들기
		SearchResultVO<ConsultantVO.Detail> result = new SearchResultVO<ConsultantVO.Detail>(
				list, totalCount, consultantSearch.getPage());
		
		return result;
	}

	/**
	 * 컨설턴트 세부사항 조회
	 */
	public ConsultantVO.Detail getDetail(int consultantNo) {
		
		// 조회
		ConsultantVO.Detail result = consultantMapper.selectOne(consultantNo);
		
		// 담당 학생 조회
		List<StudentVO.Detail> responsibility = studentMapper.selectResponsibility(consultantNo);
		result.setResponsibility(responsibility);
		
		return result;
	}
	
}
