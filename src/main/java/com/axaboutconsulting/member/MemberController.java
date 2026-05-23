package com.axaboutconsulting.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member")
public class MemberController {
	private final MemberService service;
	public MemberController(MemberService service) {
		this.service = service;
	}
	
	/**
	 * 로그인 페이지로
	 */
	@GetMapping("/login")
	public String GoLogin() {
		return "member/login";
	}

	/**
	 * 회원 가입 페이지로 가세요라
	 * @return 회원가입 페이지
	 */
	@GetMapping("/join")
	public String goJoin(Model model) {
		// thymeleaf에서 th:object로 받아갈 빈 객체 보내기
		model.addAttribute("memberJoin", new MemberVO.Join());
		return "member/join";
	}
	
	/**
	 * @param member
	 * @param bindingResult
	 * BindingResult : @Valid 뒤에 붙여나와 오류 발생 시 결과 저장
	 * @return 실패하면 기존 페이지, 성공하면 메인화면
	 */
	@PostMapping("/join")
	public String join(@Valid MemberVO.Join member
			,BindingResult bindingResult
			,Model model){
		
		// 유효성 검사 실패하면 가세요라
		if(bindingResult.hasErrors()) {
			model.addAttribute("memberJoin", new MemberVO.Join());
			return "member/join";
		}
		
		service.join(member);
		
		return "redirect:/";
	}
	
}
