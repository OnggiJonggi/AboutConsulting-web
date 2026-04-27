package com.axaboutconsulting.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.axaboutconsulting.member.model.service.MemberAccountService;
import com.axaboutconsulting.member.model.vo.Member;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberAccountController {

	@Autowired
	private MemberAccountService service;
	
	/**
	 * 로그인 페이지로 이동 - 나중에 spring security로 대체해야함
	 */
	@GetMapping("/login")
	public String login(Model model) {
		// thymeleaf에서 th:object로 받아갈 빈 객체 보내기
		model.addAttribute("login", new Member.Login());
		return "member/new-account";
	}
	
	/**
	 * 로그인 로직 - 나중에 spring security로 대체해야함
	 * 
	 * @param member
	 * @param bindingResult
	 * BindingResult : @Valid 뒤에 붙여나와 오류 발생 시 결과 저장
	 * @return 실패하면 기존 페이지, 성공하면 메인화면
	 */
	@PostMapping("login")
	public String login(@Valid Member.Login member
			,BindingResult bindingResult) {
		
		// 유효성 검사 실패하면 가세요라
		if(bindingResult.hasErrors()) return "member/login";
		
		service.login(member);
		return "redirect:/";
	}
}
