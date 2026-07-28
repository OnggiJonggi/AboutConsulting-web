package com.ax.student;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ax.consultant.ConsultantService;
import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.role.CanAccess;
import com.ax.global.security.role.HasRole;
import com.ax.global.security.role.RoleEnum;
import com.ax.student.mock.MockService;
import com.ax.student.mock.MockVO;
import com.ax.student.record.RecordService;
import com.ax.student.record.RecordVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {
	private final StudentService studentService;
	private final ConsultantService consultantService;
	private final RecordService recordService;
	private final MockService mockService;
	private final CryptoComponent cryptoComponent;
	
	/**
	 * 학생 목록 조회 페이지로
	 * 
	 * 관리자
	 * 
	 * 전형적인 검색 필터 - 검색 결과 - 페이징 바 레이이웃
	 */
	@CanAccess(RoleEnum.ADMIN)
	@GetMapping("")
	public String getList(Model model) throws Exception {
		
		// thymeleaf에서 th:object로 받아갈 빈 객체
		StudentVO.Search studentSearch = new StudentVO.Search();
		model.addAttribute("studentSearch", studentSearch);
		
		// 초기 검색
		SearchResultVO<StudentVO.Detail> result = studentService.getList(studentSearch);
		
		// 학생 식별번호 암호화
		for(StudentVO.Detail student : result.getList()) {
			student.setEncStudentNo(cryptoComponent.encrypt(student.getStudentNo()));
			student.setStudentNo(0);
		}
		
		model.addAttribute("studentList", result);
		
		return "student/list";
	}

	/**
	 * 학생 등록 페이지로
	 * 
	 * 관리자
	 * 
	 * 이름, 학교명, 학년, 학기, 계열, 목표 대학 및 전공 입력
	 * 학교명은 DB 조회(검색 버튼) 혹은 나이스 API사용(학교 추가하기 버튼)
	 */
	@CanAccess(RoleEnum.ADMIN)
	@GetMapping("register")
	public String goRegister(Model model) {
		model.addAttribute("studentRegister", new StudentVO.Insert());
		return "student/register";
	}
	
	/**
	 * 학생 등록
	 * 
	 * 관리자
	 * 
	 * 등록 성공 시 등록한 학생 상세페이지로 이동
	 */
	@CanAccess(RoleEnum.ADMIN)
	@PostMapping("register")
	public String register(
			@ModelAttribute @Valid StudentVO.Insert studentRegister,
			BindingResult bindingResult,
			HttpSession session,
			Model model)throws Exception {
		
		// 유효성 통과 못함
		if(bindingResult.hasErrors()) {
			model.addAttribute("studentRegister", new StudentVO.Insert());
			return "student/register";
		}
		
		// 학생 등록, 학생 번호 반환
		int studentNo = studentService.register(studentRegister);
		String encStudentNo = cryptoComponent.encrypt(studentNo);
		
		// 등록한 학생 상세 페이지로
		return "redirect:/student/"+encStudentNo;
	}
	
	
	/**
	 * 학생 상세 메인 페이지로
	 * 
	 * 관리자
	 * 컨설턴트 : 담당 학생
	 */
	@CanAccess({RoleEnum.ADMIN, RoleEnum.CONSULTANT})
	@GetMapping("{encStudentNo}")
	public String goView(
			@PathVariable String encStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@HasRole(RoleEnum.CONSULTANT) boolean hasRole,
			Model model) throws Exception {
		
		// thymeleaf에서 th:object로 받아갈 빈 객체
		model.addAttribute("studentRegistor", new StudentVO.Insert());
		
		int studentNo = cryptoComponent.decrypt(encStudentNo);
		
		// 컨설턴트라면 담당 학생인지 확인
		if(hasRole) {
			int consultantNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
			boolean inCharge = consultantService.isInCharge(consultantNo, studentNo);
			if(inCharge) throw new CustomException(ErrorCodeEnum.NOT_YOUR_STUDENT);
		}
		
		// 네비 바에게 여기가 어디고 나는 누구인지 알려줌
		model.addAttribute("studentMenu", "basic");
		model.addAttribute("encStudentNo", encStudentNo);
		
		// 학생 기본 정보 조회
		StudentVO.Detail detail = studentService.getStudentBasicInfo(studentNo);
		
		// 없어? 404로 가세요라
		if(detail==null)
			throw new CustomException(ErrorCodeEnum.CANNOT_FIND_STUDENT);
		
		// 컨설턴트 식별번호 있으면 암호화
		if(detail.getConsultantNo()!=0) {
			detail.setEncConsultantNo(cryptoComponent.encrypt(detail.getConsultantNo()));
			detail.setConsultantNo(0);
		}
		// 내부 식별번호 비우기
		detail.setStudentNo(0);
		
		
		model.addAttribute("studentBasicInfo", detail);
		
		return "student/view/main";
	}
	
	/**
	 * 학생 상세 - 생기부 조각 응답
	 * 
	 * 관리자
	 * 컨설턴트 : 담당 학생
	 */
	@CanAccess({RoleEnum.ADMIN, RoleEnum.CONSULTANT})
	@GetMapping("{encStudentNo}/record")
	public String getRecordFragment(
			@PathVariable String encStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@HasRole(RoleEnum.ADMIN) boolean hasRole,
			Model model) throws Exception {
		
		// 메뉴바 위치 알려주기
		model.addAttribute("studentMenu", "record");
		
		model.addAttribute("encStudentNo", encStudentNo);
		
		// 생기부 조회
		RecordVO.Detail result = recordService.getRecord(cryptoComponent.decrypt(encStudentNo));
		model.addAttribute("studentRecord", result);
		
		// 관리자라면 수정용 객체 - thymeleaf에서 th:object로 받아갈 빈 객체
		if(hasRole) {
			model.addAttribute("studentRegistor", new StudentVO.Insert());
		}
		
		return "student/view/record :: content";
	}
	
	/**
	 * 학생 상세 - 모의고사 조각 응답
	 * 
	 * 관리자
	 * 컨설턴트 : 담당 학생
	 */
	@CanAccess({RoleEnum.ADMIN, RoleEnum.CONSULTANT})
	@GetMapping("{encStudentNo}/mock")
	public String getMockFragment(
			@PathVariable String encStudentNo
			,Model model) throws Exception {
		
		model.addAttribute("studentMenu", "mock");
		model.addAttribute("encStudentNo", encStudentNo);
		
		// 모의고사 조회
		List<MockVO.Detail> mockList = mockService.getMockScoreList(cryptoComponent.decrypt(encStudentNo));
		
		// 모의고사 묶음 식별번호 암호화
		if(mockList!=null && !mockList.isEmpty()) {
			for(MockVO.Detail detail : mockList) {
				detail.setEncMockNo(cryptoComponent.encrypt(detail.getMockNo()));
				detail.setMockNo(0);
			}
		}
		model.addAttribute("mockList", mockList);
		
		return "student/view/mock :: content";
	}
	
}
