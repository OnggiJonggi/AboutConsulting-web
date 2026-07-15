package com.ax.consultant;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.org.OrgService;
import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleEnum;
import com.ax.student.StudentService;
import com.ax.student.StudentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/consultant")
@RequiredArgsConstructor
@Slf4j
public class ConsultantController {
	private final ConsultantService consultantService;
	private final OrgService orgService;
	private final StudentService studentService;
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
		
		// 암호화
		for(ConsultantVO.Detail item : result.getList()) {
			item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
			item.setConsultantNo(0);
		}
		
		model.addAttribute("consultantList", result);
		return "consultant/list";
	}
	
	/**
	 * 컨설턴트 세부사항으로
	 * 관리자
	 * 컨설턴트 : 같은 소속
	 */
	@GetMapping({"/{encConsultantNo}", "/myinfo"})
	public String goInfo(
			@PathVariable(required = false) String encConsultantNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception {
		
		int consultantNo;
		
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))) {
			
			// 관리자면 encConsultantNo에서 컨설턴트 식별번호 추출
			consultantNo = cryptoComponent.decrypt(encConsultantNo);
			
		}else if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.CONSULTANT.getPrefix()))) {
			
			if(encConsultantNo==null) {
				// 컨설턴트가 본인 페이지로 온 경우
				consultantNo = cryptoComponent.decrypt(userDetails.getEncMemberNo()); 
			}else {
				// 컨설턴트가 같은 소속 다른 컨설턴트 페이지로 온 경우
				consultantNo = cryptoComponent.decrypt(encConsultantNo);
				
				// 같은 소속인지 확인
				int memberNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
				boolean isSameOrg = orgService.isSameOrg(consultantNo, memberNo);
				if(!isSameOrg) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			}
			
			// 그도 아니면 어케들어왔노 끄져라
		}else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		
		// 조회
		ConsultantVO.Detail result = consultantService.getDetail(consultantNo);
		
		// 컨설턴트 식별번호 정리
		result.setConsultantNo(0);
		result.setEncConsultantNo(encConsultantNo);
		
		// 담당 학생 있으면 학생 식별번호 정리
		if(result!=null && result.getCharged()!=null && !result.getCharged().isEmpty()) {
			for(StudentVO.Detail item : result.getCharged()) {
				item.setEncStudentNo(cryptoComponent.encrypt(item.getStudentNo()));
				item.setStudentNo(0);
			}
		}
		
		model.addAttribute("consultantDetail", result);
		
		return "consultant/view";
	}
	
	/**
	 * 컨설턴트 - 학생 연결 페이지로
	 * 관리자
	 */
	@GetMapping("charged")
	public String goCharged(
			@RequestParam(required=false) String encConNo,
			@RequestParam(required=false) String encStuNo,
			Model model) throws Exception{
		
		
		// 검색용 객체 보내기
		StudentVO.Search studentSearch = new StudentVO.Search();
		model.addAttribute("studentSearch", studentSearch);
		ConsultantVO.Search consultantSearch = new ConsultantVO.Search();
		model.addAttribute("consultantSearch", consultantSearch);
		
		
		// 컨설턴트 세부사항에서 왔을 경우
		if(encConNo!=null) {
			model.addAttribute("encConsultantNo", encConNo);
			// 컨설턴트 조회 생략
			
		}else {
			// 컨설턴트 조회
			SearchResultVO<ConsultantVO.Detail> consultantResult = consultantService.getList(consultantSearch);
			
			// 암호화
			for(ConsultantVO.Detail item : consultantResult.getList()) {
				item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
				item.setConsultantNo(0);
			}
			
			model.addAttribute("consultantList", consultantResult);
		}
		
		// 학생 세부사항에서 왔을 경우
		if(encStuNo!=null) {
			model.addAttribute("encStudentNo", encStuNo);
			// 학생 조회 생략
			
		}else {
			// 학생 조회
			studentSearch.setIsCharged(false);
			SearchResultVO<StudentVO.Detail> studentResult = studentService.getList(studentSearch);
			
			// 암호화
			for(StudentVO.Detail item : studentResult.getList()) {
				item.setEncStudentNo(cryptoComponent.encrypt(item.getStudentNo()));
				item.setStudentNo(0);
			}
			
			model.addAttribute("studentResult", studentResult);
		}
		
		return "consultant/charged";
	}
}
