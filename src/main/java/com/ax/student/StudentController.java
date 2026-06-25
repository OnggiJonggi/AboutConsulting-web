package com.ax.student;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.ConsultantService;
import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleEnum;
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
	 * 학생 목록 조회
	 * 관리자
	 */
	@GetMapping("")
	public String getList(Model model) throws Exception {
		
		StudentVO.Search studentSearch = new StudentVO.Search();
		model.addAttribute("studentSearch", studentSearch);
		
		// 검색해요
		SearchResultVO<StudentVO.Detail> result = studentService.getList(studentSearch);
		
		// 학생 식별번호 암호화
		for(StudentVO.Detail student : result.getList()) {
			student.setEncryptedStudentNo(cryptoComponent.encrypt(String.valueOf(student.getStudentNo())));
			student.setStudentNo(0);
		}
		
		model.addAttribute("studentList", result);
		
		return "student/list";
	}

	/**
	 * 학생 등록 페이지로
	 * 관리자
	 */
	@GetMapping("register")
	public String goRegister(Model model) {
		model.addAttribute("studentRegister", new StudentVO.Insert());
		return "student/register";
	}
	
	/**
	 * 학생 등록
	 * 관리자
	 */
	@PostMapping("register")
	public String register(
			@ModelAttribute @Valid StudentVO.Insert studentRegister
			,BindingResult bindingResult
			,HttpSession session
			,Model model)throws Exception {
		
		// 유효성 통과 못함
		if(bindingResult.hasErrors()) {
			model.addAttribute("studentRegister", new StudentVO.Insert());
			return "student/register";
		}
		
		// 학생 등록, 학생 번호 반환
		int studentNo = studentService.register(studentRegister);
		String encStudentNo = cryptoComponent.encrypt(String.valueOf(studentNo));
		
		// 등록한 학생 상세 페이지로
		return "redirect:/student/"+encStudentNo;
	}
	
	
	/**
	 * 학생 상세 메인 페이지로
	 * 관리자
	 * 컨설턴트 : 담당 학생
	 */
	@GetMapping("{encryptedStudentNo}")
	public String goView(
			@PathVariable String encryptedStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception {
		
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// 컨설턴트라면 담당 학생인지 확인
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.CONSULTANT.getPrefix()))) {
			int consultantNo = Integer.valueOf(cryptoComponent.decrypt(userDetails.getEncryptedMemberNo()));
			
			boolean inCharge = consultantService.isInCharge(consultantNo, studentNo);
			if(inCharge) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			
			// 덤으로 기본정보 수정용 StudentVO.Registor객체 넣기
			model.addAttribute("studentRegistor", new StudentVO.Insert());
			
		}else if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))){
			
			// 관리자여도 StudentVO.Registor객체 넣기
			model.addAttribute("studentRegistor", new StudentVO.Insert());
		}
		
		// 네비 바에게 여기가 어디고 나는 누구인지 알려줌
		model.addAttribute("studentMenu", "basic");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 학생 기본 정보 조회
		StudentVO.Detail detail = studentService.getStudentBasicInfo(studentNo);
		
		// 없어? 404로 가세요라
		if(detail==null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 컨설턴트 식별번호 있으면 암호화
		if(detail.getConsultantNo()!=0) {
			detail.setEncConsultantNo(cryptoComponent.encrypt(String.valueOf(detail.getConsultantNo())));
			detail.setConsultantNo(0);
		}
		// 내부 식별번호 비우기
		detail.setStudentNo(0);
		
		
		model.addAttribute("studentBasicInfo", detail);
		
		return "student/view/main";
	}
	
	/**
	 * 학생 상세 - 생기부 조각 응답
	 * 관리자/담당 컨설턴트/학생 자신
	 * @param encryptedStudentNo
	 */
	@GetMapping("{encryptedStudentNo}/record")
	public String getRecordFragment(
			@PathVariable String encryptedStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception {
		
		model.addAttribute("studentMenu", "record");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 생기부 조회
		RecordVO.Detail result = recordService.getRecord(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)));
		model.addAttribute("studentRecord", result);
		
		// 관리자라면 수정용 StudentVO.Registor객체 보내주기
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))) {
			
			model.addAttribute("studentRegistor", new StudentVO.Insert());
		}
		
		return "student/view/record :: content";
	}
	
	/**
	 * 학생 상세 - 모의고사 조각 응답
	 * 관리자/담당 컨설턴트/학생 자신
	 * @param encryptedStudentNo
	 */
	@GetMapping("{encryptedStudentNo}/mock")
	public String getMockFragment(
			@PathVariable String encryptedStudentNo
			,Model model) throws Exception {
		
		model.addAttribute("studentMenu", "mock");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 모의고사 조회
		List<MockVO.Detail> mockList = mockService.getMockScoreList(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)));
		
		// 모의고사 묶음 식별번호 암호화
		if(mockList!=null && !mockList.isEmpty()) {
			for(MockVO.Detail detail : mockList) {
				detail.setEncryptedMockNo(cryptoComponent.encrypt(String.valueOf(detail.getMockNo())));
				detail.setMockNo(0);
			}
		}
		model.addAttribute("mockList", mockList);
		
		return "student/view/mock :: content";
	}
	
}
