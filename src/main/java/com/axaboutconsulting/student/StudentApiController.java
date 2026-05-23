package com.axaboutconsulting.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.axaboutconsulting.global.common.SearchResultVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentApiController {
	private StudentService service;
	private ApiRecordService recordAnalysisService;


	/**
	 * 생기부 업로드
	 */
	@PostMapping("/{encryptedStudentNo}/record/upload")
	public ResponseEntity<Void> recordUpload(
			@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,MultipartFile file) throws Exception{
		
		// 비동기 작업
		recordAnalysisService.uploadRecord(encryptedStudentNo, file.getBytes());
		
		return ResponseEntity.ok().build();
	}
	
	
	/**
	 * 학생 목록 조회
	 * 컨설턴트/관리자
	 * @param studentSearch
	 * @return 200 + SearchResultVO
	 * @throws Exception 
	 */
	@PostMapping("/list")
	public ResponseEntity<SearchResultVO<StudentVO.SearchResult>> list(StudentVO.Search studentSearch) throws Exception{
		return ResponseEntity.ok(service.getList(studentSearch));
	}

}
