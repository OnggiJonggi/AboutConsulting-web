package com.ax.consultant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleEnum;
import com.ax.student.StudentVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/consultant")
@RequiredArgsConstructor
public class ConsultantController {
	private final ConsultantService consultantService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 컨설턴트 목록 조회 화면으로
	 * 관리자
	 */
	@GetMapping("")
	public String list(Model model) throws Exception {
		
		// thymeleaf용 빈 객체 보내기
		ConsultantVO.Search consultantSearch = new ConsultantVO.Search();
		model.addAttribute("consultantSearch", consultantSearch);
		
		// 조회
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(consultantSearch);
		
		// 없으면 404
		if(result==null || result.getList()==null || result.getList().isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 암호화
		for(ConsultantVO.Detail item : result.getList()) {
			item.setEncryptedConsultantNo(cryptoComponent.encrypt(String.valueOf(item.getConsultantNo())));
			item.setConsultantNo(0);
		}
		
		model.addAttribute("consultantList", result);
		return "consultant/list";
	}
	
	/**
	 * 컨설턴트 세부사항으로
	 * 관리자
	 * 컨설턴트 : 본인
	 */
	@GetMapping({"/{encryptedConsultantNo}", "/myinfo"})
	public String goInfo(
			@PathVariable(required = false) String encryptedConsultantNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception {
		
		int consultantNo;
		
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))) {
			
			// 관리자면 encryptedConsultantNo에서 컨설턴트 식별번호 추출
			consultantNo = Integer.valueOf(cryptoComponent.decrypt(encryptedConsultantNo));
			
		}else if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))) {
			
			// 컨설턴트면 로그인 계정에서 컨설턴트 식별번호(=회원 식별번호) 추출
			consultantNo = Integer.valueOf(cryptoComponent.decrypt(userDetails.getEncryptedMemberNo())); 
			
			// 그도 아니면 어케들어왔노 끄져라
		}else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
		// 조회
		ConsultantVO.Detail result = consultantService.getDetail(consultantNo);
		
		// 컨설턴트 식별번호 정리
		result.setConsultantNo(0);
		result.setEncryptedConsultantNo(encryptedConsultantNo);
		
		// 담당 학생 있으면 학생 식별번호 정리
		if(result!=null && result.getResponsibility()!=null && !result.getResponsibility().isEmpty()) {
			for(StudentVO.Detail item : result.getResponsibility()) {
				item.setEncryptedStudentNo(cryptoComponent.encrypt(String.valueOf(item.getStudentNo())));
				item.setStudentNo(0);
			}
		}
		
		model.addAttribute("consultantDetail", result);
		
		return "consultant/view";
	}
}
