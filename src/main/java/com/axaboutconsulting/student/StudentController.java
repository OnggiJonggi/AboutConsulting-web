package com.axaboutconsulting.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentController {
	@Autowired
	private StudentService service;
	
	@GetMapping("/add")
	public String newStudent(Model model) {
		model.addAttribute("addStudent", new StudentVO.Add());
		return "student/add";
	}
}
