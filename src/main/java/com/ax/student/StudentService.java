package com.ax.student;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.security.CryptoComponent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService{
	private final StudentMapper studentMapper;
	private final TargetInfoMapper targetInfoMapper;
	private final CryptoComponent cryptoComponent;
	
	
	/**
	 * 학생 등록
	 */
	@Transactional
	public String register(StudentVO.Register studentRegister) throws Exception {
		
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
	public SearchResultVO<StudentVO.SearchResult> getList(StudentVO.Search studentSearch) throws Exception {
		
		SearchResultVO<StudentVO.SearchResult> searchResult = new SearchResultVO<StudentVO.SearchResult>(
				studentMapper.selectStudentList(studentSearch)
				,studentMapper.selectStudentListTotalCount(studentSearch)
				,studentSearch.getPage()
				);
		
		for(StudentVO.SearchResult result : searchResult.getList()) {
			result.setEncryptedStudentNo(cryptoComponent.encrypt(String.valueOf(result.getStudentNo())));
			result.setStudentNo(0);
		}
		
		return searchResult;
	}


	/**
	 * 학생 기본 정보 조회
	 */
	public StudentVO.Detail getStudentBasicInfo(String encryptedStudentNo) throws Exception {
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		return studentMapper.selectStudent(studentNo);
	}

}
