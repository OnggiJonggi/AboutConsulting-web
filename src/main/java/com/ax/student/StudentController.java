package com.ax.student;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
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
		model.addAttribute("studentList", result);
		
		return "student/list";
	}

	/**
	 * 학생 등록 페이지로
	 * 관리자
	 */
	@GetMapping("/register")
	public String goRegister(Model model) {
		model.addAttribute("studentRegister", new StudentVO.Register());
		return "student/register";
	}
	
	/**
	 * 학생 등록
	 * 관리자
	 */
	@PostMapping("/register")
	public String register(
			@RequestParam @Valid StudentVO.Register studentRegister
			,BindingResult bindingResult
			,HttpSession session
			,Model model)throws Exception {
		
		// 유효성 통과 못함
		if(bindingResult.hasErrors()) {
			model.addAttribute("studentRegister", new StudentVO.Register());
			return "student/register";
		}
		return "redirect:/student/"+studentService.register(studentRegister);
	}
	
	
	/**
	 * 학생 상세 메인 페이지로
	 * 관리자
	 * 컨설턴트 : 담당 학생
	 */
	@GetMapping("/{encryptedStudentNo}")
	public String goView(
			@PathVariable String encryptedStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception {
		
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// 네비 바에게 여기가 어디고 나는 누구인지 알려줌
		model.addAttribute("studentMenu", "basic");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 학생 기본 정보 조회
		StudentVO.Detail studentBasicInfo = studentService.getStudentBasicInfo(studentNo);
		
		// 없어? 404로 가세요라
		if(studentBasicInfo==null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		model.addAttribute("studentBasicInfo", studentBasicInfo);
		
		return "student/view/main";
	}
	
	/**
	 * 학생 상세 - 생기부 조각 응답
	 * 관리자/담당 컨설턴트/학생 자신
	 * @param encryptedStudentNo
	 */
	@GetMapping("/{encryptedStudentNo}/record")
	public String getRecordFragment(@PathVariable String encryptedStudentNo
			,Model model) throws Exception {
		model.addAttribute("studentMenu", "record");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 생기부 조회
		RecordVO.Detail result = recordService.getRecord(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)));
		model.addAttribute("studentRecord", result);
		
		return "student/view/record :: content";
	}
	
	/**
	 * 학생 상세 - 모의고사 조각 응답
	 * 관리자/담당 컨설턴트/학생 자신
	 * @param encryptedStudentNo
	 */
	@GetMapping("/{encryptedStudentNo}/mock")
	public String getMockFragment(@PathVariable String encryptedStudentNo
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
