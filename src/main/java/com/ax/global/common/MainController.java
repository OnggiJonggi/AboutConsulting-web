package com.ax.global.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	/**
	 * 메인 화면으로 끄져라.
	 * 
	 * 비 로그인, 로그인, 관리자, 컨설턴트에 따른 페이지 분기 
	 */
	@GetMapping("")
	public String main() {
		return "common/main";
	}
}
