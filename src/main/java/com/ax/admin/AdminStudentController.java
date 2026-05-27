package com.ax.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/student")
@RequiredArgsConstructor
public class AdminStudentController {
	private final AdminStudentService service;
	

}
