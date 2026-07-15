package com.ax.consultant.org;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.ConsultantService;
import com.ax.consultant.ConsultantVO;
import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleEnum;
import com.ax.member.MemberStatusEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/org")
@RequiredArgsConstructor
@Slf4j
public class OrgController {
	private final OrgService orgService;
	private final ConsultantService consultantService;
	private final CryptoComponent cryptoComponent;
	
	
	/**
	 * 소속 목록 페이지로
	 * 관리자
	 */
	@GetMapping("")
	public String goOrgList(Model model) throws Exception {
		
		// 조회용 객체 삽입
		OrgVO.Search search = new OrgVO.Search();
		model.addAttribute("orgSearch", search);
		
		// 목록 조회
		SearchResultVO<OrgVO.Detail> result = orgService.getList(search);
		
		// 대표 컨설턴트 식별번호, 소속 식별번호 암호화
		if(result!= null && result.getList()!=null && !result.getList().isEmpty()) {
			for(OrgVO.Detail item : result.getList()) {
				item.setEncLeaderNo(cryptoComponent.encrypt(item.getLeaderNo()));
				item.setEncOrgNo(cryptoComponent.encrypt(item.getOrgNo()));
				item.setOrgNo(0);
				item.setLeaderNo(0);
			}
		}
		
		model.addAttribute("orgList", result);
		
		
		return "org/list";
	}
	
	/**
	 * 소속 상세 페이지로
	 * 관리자, 컨설턴트 : 본인 소속
	 */
	@GetMapping({"{encOrgNo}", "myinfo"})
	public String goOrg(
			@PathVariable(required=false) String encOrgNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model)throws Exception {
		
		int orgNo = 0;
		
		if(encOrgNo!=null
				&& userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.ADMIN.getPrefix()))) {
			// org/{encOrgNo} 로 접근 - 관리자만 가능
			orgNo = cryptoComponent.decrypt(encOrgNo);
			model.addAttribute("encOrgNo", encOrgNo);
		}else {
			// org/myinfo 로 접근
			// 이 컨설턴트 소속 식별번호 추출
			int memberNo =  cryptoComponent.decrypt(userDetails.getEncMemberNo());
			orgNo = orgService.isBelong(memberNo);
			model.addAttribute("encOrgNo", cryptoComponent.encrypt(orgNo));
		}
		
		if(orgNo==0) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 정보 조회
		OrgVO.Detail detail = orgService.getDetail(orgNo);
		
		// 식별번호 암호화
		detail.setOrgNo(0);
		for(ConsultantVO.Detail item : detail.getConsultantDetail()) {
			item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
			item.setConsultantNo(0);
		}
		
		model.addAttribute("orgBasicInfo", detail);
		
		return "org/view";
	}
	
	
	/**
	 * 소속 생성 페이지로
	 * 관리자
	 */
	@GetMapping("register")
	public String goRegister(Model model) throws Exception {
		
		// 등록용 객체 삽입
		model.addAttribute("OrgInsert", new OrgVO.Insert());
		
		// 검색용 객체 삽입
		ConsultantVO.Search search = new ConsultantVO.Search();
		model.addAttribute("ConsultantSearch", search);
		
		search.setStatus(MemberStatusEnum.ACTIVE); // 활성화된 계정만
		search.setHasOrg(false); // 소속이 없는 컨설턴트만
		search.setInCharged(null); // 담당 학생은 미검색
		
		// 검색
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(search);
		
		// 암호화
		if(result!=null && result.getList()!=null && !result.getList().isEmpty()) {
			for(ConsultantVO.Detail item : result.getList()) {
				item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
				item.setConsultantNo(0);
			}
		}
		
		
		model.addAttribute("SearchResult", result);
		
		
		return "org/register";
	}
	
	/**
	 * 소속 생성
	 * 관리자
	 */
	@PostMapping("register")
	public String register(
			@ModelAttribute @Valid OrgVO.Insert insert,
			BindingResult bindingResult,
			Model model) throws Exception{
		
		// 유효성 검사 실패하면 가세요라
		if(bindingResult.hasErrors()) {
			model.addAttribute("OrgInsert", new OrgVO.Insert());
			return "org/register";
		}
		
		// 대표 식별번호 복호화
		insert.setLeaderNo(cryptoComponent.decrypt(insert.getEncLeaderNo()));
		insert.setEncLeaderNo(null);
		
		// 대표 식별번호가 소속 컨설턴트 식별번호에 있는지 확인
		boolean hasLeaderNo = false;
		
		// 소속될 컨설턴트 식별번호 복호화
		Set<String> encConNos = insert.getEncConsultantNos();
		Set<Integer> conNos = new HashSet<Integer>(); 
		
		for(String item : encConNos) {
			int conNo = cryptoComponent.decrypt(item);
			conNos.add(conNo);
			
			// 컨설턴트 번호가 리더 번호와 같아요!
			if(conNo==insert.getLeaderNo()) hasLeaderNo=true;
		}
		
		// 소속 컨설턴트 중 리더가 없거나 아무도 없어요
		if(!hasLeaderNo) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		insert.setConsultantNos(conNos);
		insert.setEncConsultantNos(null);
		
		// 소속 생성 및 소속 식별번호 반환
		int orgNo = orgService.register(insert);
		String encOrgNo = cryptoComponent.encrypt(orgNo);
		return "redirect:/org/"+encOrgNo;
	}
	
	/**
	 * 소속 - 컨설턴트 배정 페이지로
	 * 관리자
	 */
	@GetMapping("charged")
	public String goCharged(
			@RequestParam(required=false) String encOrgNo,
			@RequestParam(required=false) String encConNo,
			Model model) throws Exception {
		
		// encOrgNo 쿼리스트링으로 있으면 그거 씀
		if(encOrgNo != null) {
			
			// 소속 세부사항 조회
			int orgNo = cryptoComponent.decrypt(encOrgNo);
			
			// 정보 조회
			OrgVO.Detail detail = orgService.getDetail(orgNo);
			
			// 식별번호 암호화
			detail.setOrgNo(0);
			for(ConsultantVO.Detail item : detail.getConsultantDetail()) {
				item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
				item.setConsultantNo(0);
			}
			
			model.addAttribute("encOrgNo", encOrgNo);
			model.addAttribute("orgBasicInfo", detail);
			
		} else {
			// 쿼리스트링에 없으면 소속 검색
			OrgVO.Search orgSearch = new OrgVO.Search();
			model.addAttribute("orgSearch", orgSearch);
			
			// 상태값 = 정상 인 소속만 조회
			orgSearch.setStatus(OrgStatusEnum.ACTIVE);
			
			// 조회
			SearchResultVO<OrgVO.Detail> orgResult = orgService.getList(orgSearch);
			if(orgResult!= null && orgResult.getList()!=null && !orgResult.getList().isEmpty()) {
				for(OrgVO.Detail item : orgResult.getList()) {
					item.setEncLeaderNo(cryptoComponent.encrypt(item.getLeaderNo()));
					item.setEncOrgNo(cryptoComponent.encrypt(item.getOrgNo()));
					item.setOrgNo(0);
					item.setLeaderNo(0);
				}
			}
			model.addAttribute("orgList", orgResult);
		}
		
		// encConNo 쿼리스트링 있을 경우
		if(encConNo!=null) {
			model.addAttribute("encConNo", encConNo);
			
			// 이 컨설턴트가 이미 배정되었나요?
			int conNo = cryptoComponent.decrypt(encConNo);
			int orgNo = orgService.isBelong(conNo);
			
			// 배정 되어 있으면 가세요라
			if(orgNo > 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
			
		} else {
			// 쿼리스트링에 없으면 컨설턴트 검색
			ConsultantVO.Search consultantSearch = new ConsultantVO.Search();
			model.addAttribute("consultantSearch", consultantSearch);
			
			// 상태값 = 정상, 소속 없는 컨설턴트만 조회 
			consultantSearch.setStatus(MemberStatusEnum.ACTIVE);
			consultantSearch.setHasOrg(false);
			
			SearchResultVO<ConsultantVO.Detail> consultantResult= consultantService.getList(consultantSearch);
			for(ConsultantVO.Detail item : consultantResult.getList()) {
				item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
				item.setConsultantNo(0);
			}
			model.addAttribute("consultantResult", consultantResult);
		}
		
		return "org/charged";
	}
}
