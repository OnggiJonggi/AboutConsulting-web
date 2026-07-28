package com.ax.consultant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.role.CanAccess;
import com.ax.global.security.role.RoleEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/consultant")
@RequiredArgsConstructor
@Slf4j
public class ConsultantApiController {
	private final ConsultantService consultantService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 컨설턴트 목록 조회
	 * 
	 * 관리자
	 */
	@CanAccess(RoleEnum.ADMIN)
	@GetMapping("")
	public ResponseEntity<SearchResultVO<ConsultantVO.Detail>> getList(
			@ModelAttribute ConsultantVO.Search consultantSearch) throws Exception{
		
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(consultantSearch);
		
		// 없으면 404
		if(result==null || result.getList()==null || result.getList().isEmpty())
			return ResponseEntity.notFound().build();
		
		// 암호화
		for(ConsultantVO.Detail item : result.getList()) {
			item.setEncConsultantNo(cryptoComponent.encrypt(item.getConsultantNo()));
			item.setConsultantNo(0);
		}
		
		return ResponseEntity.ok(result);
	}
	
	
	/**
	 * 컨설턴트 - 학생 삭제
	 * 
	 * 관리자
	 */
	@CanAccess(RoleEnum.ADMIN)
	@DeleteMapping("{encConsultantNo}/charged")
	public ResponseEntity<Void> deleteCharged(
			@RequestParam String encStudentNo,
			@PathVariable String encConsultantNo
			) throws Exception{
		
		int consultantNo = cryptoComponent.decrypt(encConsultantNo);
		int studentNo = cryptoComponent.decrypt(encStudentNo);
		
		consultantService.deleteCharged(consultantNo, studentNo);
		
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * 컨설턴트 - 학생 연결
	 * 
	 * 관리자
	 */
	@CanAccess(RoleEnum.ADMIN)
	@PostMapping("charged")
	public ResponseEntity<Void> insertCharged(
			@RequestParam String encConsultantNo,
			@RequestParam List<String> encStudentNos
			) throws Exception{
		
		// 복호화
		int consultantNo = cryptoComponent.decrypt(encConsultantNo);
		Set<Integer> studentNos = new HashSet<Integer>();
		for(String item : encStudentNos) {
			studentNos.add(cryptoComponent.decrypt(item));
		}
		
		// 식별번호 없어요? 없는데 왜 왔어요?
		if(consultantNo==0
				|| studentNos==null
				|| studentNos.isEmpty()
				|| studentNos.contains(0))
			return ResponseEntity.badRequest().build();
		
		consultantService.insertCharged(consultantNo, studentNos);
		
		return ResponseEntity.ok().build();
	}
	

}
