package com.ax.consultant;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SearchResultVO;
import com.ax.student.StudentMapper;
import com.ax.student.StudentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
		List<StudentVO.Detail> charged = studentMapper.selectCharged(consultantNo);
		result.setCharged(charged);
		
		return result;
	}

	/**
	 * 컨설턴트 - 학생 추가
	 * 
	 * @param studentNo
	 * @param consultantNo
	 */
	public void insertCharged(int consultantNo, Set<Integer> studentNos) {
		
		// 식별번호 없어요? 없는데 왜 왔어요?
		if(consultantNo==0
				|| studentNos==null
				|| studentNos.isEmpty()
				|| studentNos.contains(0))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		// 담당 컨설턴트가 이미 있다 자수합니다. 자수하고 광명찾자
		int isDupli = consultantMapper.selectIsCharged(consultantNo, studentNos);
		if(isDupli>0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		// 삽입
		int result = consultantMapper.insertCharged(consultantNo, studentNos);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 컨설턴트 - 학생 삭제
	 * 
	 * @param consultantNo
	 * @param studentNo
	 */
	public void deleteCharged(int consultantNo, int studentNo) {
		int result = consultantMapper.deleteCharged(consultantNo, studentNo);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
