package com.axaboutconsulting.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.axaboutconsulting.global.common.SearchResultVO;
import com.axaboutconsulting.global.security.CryptoComponent;

@Service
public class StudentServiceImpl implements StudentService{
	private final StudentMapper studentMapper;
	private final TargetInfoMapper targetUnivMapper;
	private final CryptoComponent cryptoComponent;
	public StudentServiceImpl(StudentMapper studentMapper, CryptoComponent cryptoComponent, TargetInfoMapper targetUnivMapper){
		this.studentMapper = studentMapper;
		this.cryptoComponent = cryptoComponent;
		this.targetUnivMapper = targetUnivMapper;
	}
	
	
	/**
	 * 학생 등록
	 */
	@Override
	@Transactional
	public String register(StudentVO.Register studentRegister) throws Exception {
		
		// 학생 등록
		studentMapper.insertStudent(studentRegister);
		
		// 목표 학과, 대학이 있는 경우
		if(studentRegister.getTarget() != null
			&& studentRegister.getTarget().getTargetMajor() != null
			&& !studentRegister.getTarget().getTargetMajor().isEmpty()) {
			// major유무만 검사하고 univ는 검사 안 함.
			// 학생 번호, 목표 대학, 학과 담당 객체 전환
			TargetInfoVO.Update targetUpdate = new TargetInfoVO.Update(
					studentRegister.getTarget()
					,studentRegister.getStudentNo());
			
			// 목표 대학 등록
			targetUnivMapper.insertTargetUniv(targetUpdate);
		}
		
		// 암호화된 학생 식별번호 반납
		return cryptoComponent.encrypt(String.valueOf(studentRegister.getStudentNo()));
	}

	@Override
	public SearchResultVO<StudentVO.Detail> getList(StudentVO.Search studentSearch) throws Exception {
		
		SearchResultVO<StudentVO.Detail> searchResult = new SearchResultVO<StudentVO.Detail>(
				studentMapper.selectStudentList(studentSearch)
				,studentMapper.selectStudentListTotalCount(studentSearch)
				,studentSearch.getPage()
				);
		
		cryptoComponent.encryptList(searchResult.getList());
		
		return searchResult;
	}


}
