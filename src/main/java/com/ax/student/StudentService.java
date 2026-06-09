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

import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.HmacComponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService{
	private final StudentMapper studentMapper;
	private final TargetInfoMapper targetInfoMapper;
	private final CryptoComponent cryptoComponent;
	private final HmacComponent hmacComponent;
	
	
	/**
	 * 학생 등록
	 */
	@Transactional
	public String register(StudentVO.Register studentRegister) throws Exception {
		
		// 목표 대학/학과 HMAC 검증
		if(studentRegister.getTarget()!=null && !studentRegister.getTarget().isEmpty()) {
			
			for(TargetInfoVO.Register target : studentRegister.getTarget()) {
				String plainText = target.getUniv()+target.getMajor();
				String hmacText = hmacComponent.hashing(plainText);
				
				// 헉학교학과해싱값과hmac필드값이다르다니대체그게무슨소리니
				if(!hmacText.equals(target.getHmac()))
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			}
		}
		
		// 학생 등록
		studentMapper.insertStudent(studentRegister);
		
		/*
		 * 지망 학교/학과 등록
		 * 지망 순위가 높은 학교가 없고 낮은 학교가 있으면 순위 당기기
		 */
		List<TargetInfoVO.Register> filtered = Optional.ofNullable(studentRegister.getTarget())
			    .orElse(Collections.emptyList())
			    .stream()
			    .filter(t -> t.getUniv() != null && !t.getUniv().trim().isEmpty())
			    .filter(t -> t.getMajor() != null && !t.getMajor().trim().isEmpty())
			    .sorted(Comparator.comparingInt(TargetInfoVO.Register::getRanking))
			    .collect(Collectors.toList());
		
		if (filtered.isEmpty())
		    throw new CustomException(ErrorCodeEnum.TARGET_REQUIRED);

		for (int i = 0; i < filtered.size(); i++) {
		    filtered.get(i).setRanking(i + 1);
		}

		targetInfoMapper.insertTarget(studentRegister.getStudentNo(), filtered);
		
		// 암호화된 학생 식별번호 반납
		return cryptoComponent.encrypt(String.valueOf(studentRegister.getStudentNo()));
	}

	/**
	 * 학생 목록 조회
	 */
	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search studentSearch) throws Exception {
		
		// 검색
		List<StudentVO.Detail> result = studentMapper.selectStudentList(studentSearch);
		
		// 검색 결과 수
		int totalCount = studentMapper.selectStudentListTotalCount(studentSearch);
		
		// SearchResultVO로 감싸기
		SearchResultVO<StudentVO.Detail> searchResult = new SearchResultVO<StudentVO.Detail>(
				result, totalCount, studentSearch.getPage());
		
		// 학생 식별번호 암호화
		for(StudentVO.Detail student : searchResult.getList()) {
			student.setEncryptedStudentNo(cryptoComponent.encrypt(String.valueOf(student.getStudentNo())));
			student.setStudentNo(0);
		}
		
		return searchResult;
	}


	/**
	 * 학생 기본 정보 조회
	 */
	public StudentVO.Detail getStudentBasicInfo(int studentNo) throws Exception {
		return studentMapper.selectStudent(studentNo);
	}

}
