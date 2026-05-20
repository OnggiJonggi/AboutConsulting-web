package com.axaboutconsulting.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.axaboutconsulting.student.StudentVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/student")
public class AdminStudentController {
	@Autowired
	private AdminStudentService service;
	

}
