package com.ax.student;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ax.global.common.SearchResultVO;
import com.ax.global.security.CryptoComponent;
import com.ax.student.mock.ApiMockService;
import com.ax.student.mock.MockService;
import com.ax.student.mock.MockStatusEnum;
import com.ax.student.record.ApiRecordService;
import com.ax.student.record.RecordService;
import com.ax.student.record.RecordStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Slf4j
public class StudentApiController {
	private final StudentService studentService;
	private final ApiRecordService apiRecordService;
	private final RecordService recordService;
	private final MockService mockService;
	private final ApiMockService apiMockService;
	private final CryptoComponent cryptoComponent;

	/**
	 * 생기부 업로드
	 * 관리자/컨설턴트/학생
	 */
	@PostMapping("/{encryptedStudentNo}/record/upload")
	public ResponseEntity<String> recordUpload(
			@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,MultipartFile file, Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		
		// 생기부 분석결과 묶음 + 비동기 요청 작업 상태값 생성
		int groupNo = recordService.createAnalysisGroup(studentNo);
		
		// 비동기 작업
		apiRecordService.analysisRecord(studentNo, groupNo, file.getBytes());
		
		return ResponseEntity.ok(cryptoComponent.encrypt(String.valueOf(groupNo)));
	}
	
	/**
	 * 분석된 생기부 있나요
	 * 관리자/컨설턴트/학생
	 * 
	 * @param encryptedStudentNo
	 */
	@GetMapping("/record/status")
	public ResponseEntity<Void> getRecordStatus(String encryptedGroupNo) throws Exception{
		int groupNo = Integer.valueOf(cryptoComponent.decrypt(encryptedGroupNo));

		RecordStatusEnum status = RecordStatusEnum.valueOf(recordService.getStatus(groupNo));
		switch(status) {
		case READY:
			return ResponseEntity.notFound().build();
		case ACTIVE:
			return ResponseEntity.ok().build();
		case FAILED:
		default:
			return ResponseEntity.badRequest().build();
			
		}
	}
	
	
	/**
	 * 모의고사 성적표 업로드
	 * 관리자/컨설턴트/학생
	 */
	@PostMapping("/{encryptedStudentNo}/mock/upload")
	public ResponseEntity<String> mockUpload(
			@PathVariable("encryptedStudentNo") String encryptedStudentNo
			,MultipartFile file, Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = Integer.valueOf(encryptedStudentNo);
		
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		int groupNo = mockService.createMockGroup(studentNo);
		
		// 비동기 작업
		apiMockService.analysisMock(studentNo, groupNo, file.getBytes());
		
		return ResponseEntity.ok(cryptoComponent.encrypt(String.valueOf(groupNo)));
	}
	
	/**
	 * 분석된 모의고사 성적표 있나요
	 * 관리자/컨설턴트/학생
	 * 
	 * @param encryptedStudentNo
	 */
	@GetMapping("/mock/status")
	public ResponseEntity<Void> getMockStatus(String encryptedGroupNo) throws Exception{
		int groupNo = Integer.valueOf(cryptoComponent.decrypt(encryptedGroupNo));

		MockStatusEnum status = MockStatusEnum.valueOf(mockService.getStatus(groupNo));
		switch(status) {
		case READY:
			return ResponseEntity.notFound().build();
		case ACTIVE:
			return ResponseEntity.ok().build();
		case FAILED:
		default:
			return ResponseEntity.badRequest().build();
		}
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
