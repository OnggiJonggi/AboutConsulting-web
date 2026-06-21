package com.ax.consultant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;

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
	 * 관리자
	 */
	@GetMapping("")
	public ResponseEntity<SearchResultVO<ConsultantVO.Detail>> getList(
			@ModelAttribute ConsultantVO.Search consultantSearch) throws Exception{
		
		SearchResultVO<ConsultantVO.Detail> result = consultantService.getList(consultantSearch);
		
		// 없으면 404
		if(result==null || result.getList()==null || result.getList().isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 암호화
		for(ConsultantVO.Detail item : result.getList()) {
			item.setEncryptedConsultantNo(cryptoComponent.encrypt(String.valueOf(item.getConsultantNo())));
			item.setConsultantNo(0);
		}
		
		return ResponseEntity.ok(result);
	}
	
	
	/**
	 * 컨설턴트 - 학생 삭제
	 * 관리자
	 * 
	 * @param encryptedStudentNo
	 * @param encryptedConsultantNo
	 */
	@DeleteMapping("{encryptedConsultantNo}/charged")
	public ResponseEntity<Void> deleteCharged(
			@RequestParam String encryptedStudentNo,
			@PathVariable String encryptedConsultantNo
			) throws Exception{
		
		int consultantNo = Integer.valueOf(cryptoComponent.decrypt(encryptedConsultantNo));
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		consultantService.deleteCharged(consultantNo, studentNo);
		
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * 컨설턴트 - 학생 연결
	 * 관리자
	 * 
	 * @param encryptedStudentNo
	 * @param encryptedConsultantNo
	 */
	@PostMapping("charged")
	public ResponseEntity<Void> insertCharged(
			@RequestParam String encryptedConsultantNo,
			@RequestParam List<String> encryptedStudentNos
			) throws Exception{
		
		// 복호화
		int consultantNo = Integer.valueOf(cryptoComponent.decrypt(encryptedConsultantNo));
		Set<Integer> studentNos = new HashSet<Integer>();
		for(String item : encryptedStudentNos) {
			studentNos.add(Integer.valueOf(cryptoComponent.decrypt(item)));
		}
		
		consultantService.insertCharged(consultantNo, studentNos);
		
		return ResponseEntity.ok().build();
	}
	

}
