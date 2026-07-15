package com.ax.student;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.org.OrgRegexp;
import com.ax.global.common.SanitizeComponent;
import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.member.MemberRegexp;
import com.ax.school.SchoolRegexp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService{
	private final StudentMapper studentMapper;
	private final TargetInfoMapper targetInfoMapper;
	private final SanitizeComponent sanitizeComponent;
//	private final HmacComponent hmacComponent;
	
	
	/**
	 * 학생 등록
	 * 
	 * 대학 및 학과를 API로 검색한다면 검색어를 제대로 파라미터에 보냈는지
	 * 검사하는 HMAC로직이 필요해요
	 * 그런데 대학 및 학과 검색 로직이 폐기되면서 HMAC도 같이 폐기됨
	 */
	@Transactional
	public int register(StudentVO.Insert Insert) throws Exception {
		
		// 목표 대학/학과 HMAC 검증(미사용)
//		if(studentRegister.getTarget()!=null && !studentRegister.getTarget().isEmpty()) {
//			
//			for(TargetInfoVO.Register target : studentRegister.getTarget()) {
//				String plainText = target.getUniv()+target.getMajor();
//				String hmacText = hmacComponent.hashing(plainText);
//				
//				// 헉학교학과해싱값과hmac필드값이다르다니대체그게무슨소리니
//				if(!hmacText.equals(target.getHmac()))
//					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//			}
//		}
		
		// 학생 등록
		studentMapper.insertStudent(Insert);
		
		/*
		 * 지망 학교/학과 등록
		 * 지망 순위가 높은 학교가 없고 낮은 학교가 있으면 순위 당기기
		 */
		List<TargetInfoVO.Insert> filtered = Optional.ofNullable(Insert.getTarget())
			    .orElse(Collections.emptyList())
			    .stream()
			    .filter(t -> t.getUniv() != null && !t.getUniv().trim().isEmpty())
			    .filter(t -> t.getMajor() != null && !t.getMajor().trim().isEmpty())
			    .sorted(Comparator.comparingInt(TargetInfoVO.Insert::getRanking))
			    .collect(Collectors.toList());
		
		if (filtered.isEmpty())
		    throw new CustomException(ErrorCodeEnum.TARGET_REQUIRED);

		for (int i = 0; i < filtered.size(); i++) {
		    filtered.get(i).setRanking(i + 1);
		}

		targetInfoMapper.insertTarget(Insert.getStudentNo(), filtered);
		
		// 학생 식별번호 반납
		return Insert.getStudentNo();
	}

	/**
	 * 학생 목록 검색
	 */
	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search search) throws Exception {
		
		// 검색어 소독
		search.setName(sanitizeComponent.searchKeyword(search.getName(), StudentRegexp.NAME_MAX_LENGTH));
		search.setTrack(sanitizeComponent.searchKeyword(search.getTrack(), StudentRegexp.TRACK_MAX_LENGTH));
		search.setSchoolName(sanitizeComponent.searchKeyword(search.getSchoolName(), SchoolRegexp.NAME_MAX_LENGTH));
		search.setTargetUniv(sanitizeComponent.searchKeyword(search.getTargetUniv(), StudentRegexp.TARGET_UNIV_MAX_LENGTH));
		search.setTargetMajor(sanitizeComponent.searchKeyword(search.getTargetMajor(), StudentRegexp.TARGET_MAJOR_MAX_LENGTH));
		search.setConsultantNickname(sanitizeComponent.searchKeyword(search.getConsultantNickname(), MemberRegexp.NAME_MAX_LENGTH));
		search.setConsultantOrgName(sanitizeComponent.searchKeyword(search.getConsultantOrgName(), OrgRegexp.NAME_MAX_LENGTH));
		
		// 검색
		List<StudentVO.Detail> result = studentMapper.selectStudentList(search);
		
		// 검색 결과 수
		int totalCount = studentMapper.selectStudentListTotalCount(search);
		
		// SearchResultVO로 감싸기
		SearchResultVO<StudentVO.Detail> searchResult = new SearchResultVO<StudentVO.Detail>(
				result, totalCount, search.getPage());
		
		return searchResult;
	}


	/**
	 * 학생 기본 정보 조회
	 */
	public StudentVO.Detail getStudentBasicInfo(int studentNo) throws Exception {
		return studentMapper.selectStudent(studentNo);
	}

	/**
	 * 학생 업데이트
	 */
	public void updateStudent(StudentVO.Insert student) {
		
		// 기본정보 업데이트
		int result1 = studentMapper.updateStudent(student);
		if(result1==0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		// 지망 학교/학과 지우기
		targetInfoMapper.delete(student.getStudentNo());
		
		/*
		 * 지망 학교/학과 등록
		 * 지망 순위가 높은 학교가 없고 낮은 학교가 있으면 순위 당기기
		 */
		List<TargetInfoVO.Insert> filtered = Optional.ofNullable(student.getTarget())
			    .orElse(Collections.emptyList())
			    .stream()
			    .filter(t -> t.getUniv() != null && !t.getUniv().trim().isEmpty())
			    .filter(t -> t.getMajor() != null && !t.getMajor().trim().isEmpty())
			    .sorted(Comparator.comparingInt(TargetInfoVO.Insert::getRanking))
			    .collect(Collectors.toList());
		
		if (filtered.isEmpty())
		    throw new CustomException(ErrorCodeEnum.TARGET_REQUIRED);

		for (int i = 0; i < filtered.size(); i++) {
		    filtered.get(i).setRanking(i + 1);
		}

		targetInfoMapper.insertTarget(student.getStudentNo(), filtered);
		
	}

	/**
	 * 학생 상태값 변경
	 */
	public void updateStatus(int studentNo, StudentStatusEnum status) {
		int result = studentMapper.updateStatus(studentNo, status);
		if(result==0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

}
