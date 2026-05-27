package com.ax.student;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ax.global.security.CryptoComponent;
import com.ax.student.mock.MockService;
import com.ax.student.mock.MockVO;
import com.ax.student.record.RecordService;
import com.ax.student.record.RecordVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
	private final StudentService studentService;
	private final RecordService recordService;
	private final MockService mockService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 학생 등록 페이지로
	 */
	@GetMapping("/register")
	public String goRegister(Model model) {
		model.addAttribute("studentRegister", new StudentVO.Register());
		return "student/register";
	}
	
	/**
	 * 학생 등록
	 */
	@PostMapping("/register")
	public String register(@Valid StudentVO.Register studentRegister
			,BindingResult bindingResult
			,Model model)throws Exception {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("studentRegister", new StudentVO.Register());
			return "student/register";
		}
		
		return "redirect:/student/"+studentService.register(studentRegister);
	}
	
	
	/**
	 * 학생 상세 메인 페이지로
	 * 관리자/컨설턴트
	 * @param encryptedStudentNo 암호화된 학생 번호
	 */
	@GetMapping("/{encryptedStudentNo}")
	public String goView(@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,Model model) throws Exception {
		
		// 네비 바에게 여기가 어디고 나는 누구인지 알려줌
		model.addAttribute("studentMenu", "basic");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 학생 기본 정보 조회
		StudentVO.Detail studentBasicInfo = studentService.getStudentBasicInfo(encryptedStudentNo);
		model.addAttribute("studentBasicInfo", studentBasicInfo);
		
		return "student/view/main";
	}
	
	/**
	 * 학생 상세 - 생기부 조각 응답
	 * @param encryptedStudentNo
	 */
	@GetMapping("/{encryptedStudentNo}/record")
	public String getRecordFragment(@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,Model model) throws Exception {
		model.addAttribute("studentMenu", "record");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 생기부 조회
		RecordVO.Detail result = recordService.getRecord(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)));
		model.addAttribute("studentRecord", result);
		
		return "student/view/record :: content";
	}
	
	/**
	 * 학생 상세 - 모의고사 조각 응답
	 * @param encryptedStudentNo
	 */
	@GetMapping("/{encryptedStudentNo}/mock")
	public String getMockFragment(@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,Model model) throws Exception {
		model.addAttribute("studentMenu", "mock");
		model.addAttribute("encryptedStudentNo", encryptedStudentNo);
		
		// 모의고사 조회
		List<MockVO.Detail> mockList = mockService.getMockScoreList(Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo)));
		
		// 모의고사 묶음 식별번호 암호화
		if(mockList!=null && !mockList.isEmpty()) {
			for(MockVO.Detail detail : mockList) {
				detail.setEncryptedMockNo(cryptoComponent.encrypt(String.valueOf(detail.getMockNo())));
				detail.setMockNo(0);
			}
		}
		model.addAttribute("mockList", mockList);
		
		return "student/view/mock :: content";
	}
	
}
