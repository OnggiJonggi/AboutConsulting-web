package com.ax.global.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	/**
	 * 메인 화면으로 끄져라.
	 */
	@GetMapping("/")
	public String main() {
		return "common/main";
	}
}
