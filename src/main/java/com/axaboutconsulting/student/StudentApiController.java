package com.axaboutconsulting.student;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	private StudentService studentService;
	private ApiRecordService recordAnalysisService;
	private RecordService recordService;


	/**
	 * 생기부 업로드
	 * 관리자/컨설턴트/학생
	 */
	@PostMapping("/{encryptedStudentNo}/record/upload")
	public ResponseEntity<Void> recordUpload(
			@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,MultipartFile file) throws Exception{
		
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		// 비동기 작업
		recordAnalysisService.uploadRecord(encryptedStudentNo, file.getBytes());
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 분석된 생기부 있나요
	 * 관리자/컨설턴트/학생
	 * @param encryptedStudentNo
	 */
	@GetMapping("/{encryptedStudentNo}/record/status")
	public ResponseEntity<Void> getStatus(@PathVariable("encryptedStudentNo") String encryptedStudentNo) throws Exception{
		
		if(recordService.isActive(encryptedStudentNo) > 1)
			return ResponseEntity.ok().build();
		else return ResponseEntity.notFound().build();
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
		return ResponseEntity.ok(studentService.getList(studentSearch));
	}

}
