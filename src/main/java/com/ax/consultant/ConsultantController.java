package com.ax.consultant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consultant")
@RequiredArgsConstructor
public class ConsultantController {
	public final ConsultantService service;

	@GetMapping("/list")
	public String list(Model model) {
		model.addAttribute("consultantSearch", new ConsultantVO.Search());
		return "consultant/list";
	}
}
