package com.axaboutconsulting.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/student")
public class AdminStudentController {
	private final AdminStudentService service;
	public AdminStudentController(AdminStudentService service) {
		this.service = service;
	}
	

}
