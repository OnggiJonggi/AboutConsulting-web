package com.axaboutconsulting.student;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
	private final StudentService studentService;
	private final RecordService recordService;

	/**
	 * 학생 등록 페이지로
	 */
	@GetMapping("/register")
	public String goRegister(Model model) {
		model.addAttribute("studentRegister", new StudentVO.Register());
		return "student/register";
	}
	
	/**
	 * 학생 등록
	 */
	@PostMapping("/register")
	public String register(@Valid StudentVO.Register studentRegister
			,BindingResult bindingResult
			,Model model)throws Exception {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("studentRegister", new StudentVO.Register());
			return "student/register";
		}
		
		return "redirect:/student/view/"+studentService.register(studentRegister);
	}
	
	
	/**
	 * 학생 상세 메인 페이지로
	 * 관리자/컨설턴트
	 * @param encryptedStudentNo 암호화된 학생 번호
	 */
	@GetMapping("/view/{encryptedStudentNo}")
	public String goView(@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,Model model) throws Exception {
		
		// 네비 바에게 여기가 어디고 나는 누구인지 알려줌
		model.addAttribute("studentMenu", "basic");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 학생 기본 정보 조회
		StudentVO.Detail studentBasicInfo = studentService.getStudentBasicInfo(encryptedStudentNo);
		model.addAttribute("studentBasicInfo", studentBasicInfo);
		
		return "student/view/main";
	}
	
	/**
	 * 학생 상세 - 생기부 조각 응답
	 * @param encryptedStudentNo
	 */
	@GetMapping("/view/{encryptedStudentNo}/record")
	public String getRecordFragment(@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,Model model) throws Exception {
		model.addAttribute("studentMenu", "record");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 생기부 조회
		RecordVO.Detail result = recordService.getRecord(encryptedStudentNo);
		model.addAttribute("studentRecord", result);
		
		return "student/view/record :: content";
	}
	
}
