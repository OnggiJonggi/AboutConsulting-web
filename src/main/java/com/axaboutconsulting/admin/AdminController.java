package com.axaboutconsulting.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
//	@Autowired
//	private AdminService service;
	
	// 관리자 메인 페이지로
	@GetMapping
	public String adminMainPage() {
		return "admin/main";
	}
}
