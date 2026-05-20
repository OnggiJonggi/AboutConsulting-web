package com.axaboutconsulting.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/student")
public class StudentController {
	@Autowired
	private StudentService service;
	
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
		
		return "redirect:/student/view"+service.register(studentRegister);
	}
	
	
	@GetMapping("/view/{encryptedStudentNo}")
	public String goView() {
		//암호화된 학생 번호를 받아야 해요!!
		
		return "company/view";
	}
	
}
