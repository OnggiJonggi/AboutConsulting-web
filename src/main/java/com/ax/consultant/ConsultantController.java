package com.ax.consultant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ax.global.common.SearchResultVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consultant")
@RequiredArgsConstructor
public class ConsultantController {
	public final ConsultantService consultantService;

	/**
	 * 컨설턴트 목록
	 * 관리자
	 */
	@GetMapping("")
	public String list(Model model) {
		ConsultantVO.Search consultantSearch = new ConsultantVO.Search();
		model.addAttribute("consultantSearch", consultantSearch);
		
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(consultantSearch);
		model.addAttribute("consultantList", result);
		return "consultant/list";
	}
}
