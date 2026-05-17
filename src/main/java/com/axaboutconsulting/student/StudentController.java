package com.axaboutconsulting.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
	 * 학생 추가 페이지로
	 * 컨설턴트 / 관리자
	 * @param StudentRegister model
	 */
	@GetMapping("/register")
	public String goRegister(Model model) {
		model.addAttribute("addStudent", new StudentVO.Register());
		return "student/add";
	}
	
	/**
	 * 학생 추가
	 * @param studentRegister
	 * @param bindingResult
	 * @return 성공 200, 유효성 검사 오류 400, 실패 500
	 */
	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid StudentVO.Register studentRegister
			,BindingResult bindingResult){
		
		if(bindingResult.hasErrors()) return ResponseEntity.badRequest().build();
		
		service.register(studentRegister);
		
		return ResponseEntity.ok().build();
	}
	
}
