package com.ax.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ax.global.common.SearchResultVO;
import com.ax.global.exception.CustomException;
import com.ax.global.exception.ErrorCodeEnum;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.role.CanAccess;
import com.ax.global.security.role.HasRole;
import com.ax.global.security.role.RoleEnum;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {
	private final MemberService memberService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 아이디 중복 확인
	 */
	@GetMapping("check-id")
	public ResponseEntity<Void> checkId(
			@RequestParam String userId) {
		
		memberService.checkId(userId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 닉네임 중복 확인	
	 */
	@GetMapping("check-nickname")
	public ResponseEntity<Void> checkNickName(
			@RequestParam String nickname) {
		
		memberService.checkNick(nickname);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 회원 목록
	 * 
	 * 관리자
	 */
	@CanAccess(RoleEnum.ADMIN)
	@GetMapping("")
	public ResponseEntity<SearchResultVO<MemberVO.Detail>> getList(
			@ModelAttribute MemberVO.Search search) throws Exception{
		
		SearchResultVO<MemberVO.Detail> result = memberService.getList(search);
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 회원 기본정보 수정
	 * 
	 * 모든 회원
	 * 관리자 : 다른 회원 수정 가능
	 */
	@PreAuthorize("isAuthenticated()")
	@PutMapping("{encMemberNo}/update")
	public ResponseEntity<Void> updateMemberBasicInfo(
			@ModelAttribute MemberVO.Update member,
			@PathVariable String encMemberNo) throws Exception{
		
		// 회원 식별번호 추출
		int memberNo = cryptoComponent.decrypt(encMemberNo);
		member.setMemberNo(memberNo);
		
		memberService.updateMemberBasicInfo(member);
		
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * 권한 수정
	 * 
	 * 최고 관리자 : 본인 계정 권한 수정 불가능
	 * 관리자 : 본인 계정 권한 수정 불가능, 관리자 권한 접근 불가능
	 */
	@CanAccess(RoleEnum.ADMIN)
	@PutMapping("{encMemberNo}/update/role")
	public ResponseEntity<Void> updateMemberRole(
			@RequestParam @NotNull RoleEnum role,
			@PathVariable String encMemberNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@HasRole(RoleEnum.ADMIN) boolean hasRole) throws Exception{
		
		// 피수정 회원 식별번호
		int memberNo = cryptoComponent.decrypt(encMemberNo);
		
		// 수정 회원 식별번호
		int myMemberNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
		
		// 최고 관리자 권한은 죽었다 깨어나도 떽! 이야.
		if(role==RoleEnum.SUPER_ADMIN) {
			log.warn("최고 관리자 권한 생성 시도 발견 memberNo : {}", myMemberNo);
			throw new CustomException(ErrorCodeEnum.CANNOT_CREATE_SUPER_ADMIN);
		}
		
		// 감히 어딜 관리자 따위가 새로운 관리자를 만드려 하는가
		if(role==RoleEnum.ADMIN && hasRole) {
			log.warn("관리자 권한 생성 시도 발견 memberNo : {}", myMemberNo);
			throw new CustomException(ErrorCodeEnum.CANNOT_CREATE_ADMIN);
		}
		
		// 자추는 추하지;;
		if(myMemberNo==memberNo) {
			log.warn("자신의 권한을 바꾸려 시도합니다 memberNo : {}", myMemberNo);
			throw new CustomException(ErrorCodeEnum.CANNOT_CREATE_ADMIN);
		}
		
		memberService.updateMemberRole(memberNo, role);
		
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * 상태값 수정
	 * 
	 * 관리자 : 본인 계정 수정 불가
	 * 
	 * 최고 관리자 상태값 수정 불가
	 */
	@CanAccess(RoleEnum.ADMIN)
	@PutMapping("/{encMemberNo}/update/status")
	public ResponseEntity<Void> updateMemberBasicInfo(
			@RequestParam @NotNull MemberStatusEnum status,
			@PathVariable String encMemberNo,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@HasRole(RoleEnum.ADMIN) boolean hasRole) throws Exception{
		
		// 피수정자 회원 식별번호 추출
		int memberNo = cryptoComponent.decrypt(encMemberNo);
		
		// 수정자 식별번호 추출
		int myMemberNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
		log.info("관리자 권한 : {}, {}, {}", hasRole, memberNo, myMemberNo);
		
		// 관리자 계정이면 본인 계정 수정 불가능
		if(hasRole && myMemberNo == memberNo) {
			log.warn("관리자가 자신의 계정을 수정하려 시도중입니다. memberNo : "+myMemberNo);
			throw new CustomException(ErrorCodeEnum.CANNOT_UPDATE_MY_STATUS);
		}
		
		// 실시!
		memberService.updateMemberStatus(memberNo, status);
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 별명 중복 확인(수정용)
	 */
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/check-updatednickname")
	public ResponseEntity<Void> checkUpdatedNickname(
			@RequestParam String nickname, 
			@RequestParam String encMemberNo) throws Exception {
		
		int memberNo = cryptoComponent.decrypt(encMemberNo);
		memberService.checkUpdatedNickname(memberNo, nickname);
		return ResponseEntity.ok().build();
	}
}
