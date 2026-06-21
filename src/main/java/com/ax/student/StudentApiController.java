package com.ax.student;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ax.global.common.SearchResultVO;
import com.ax.global.file.FileDataVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.student.mock.MockService;
import com.ax.student.mock.MockStatusEnum;
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
	private final RecordService recordService;
	private final MockService mockService;
	private final CryptoComponent cryptoComponent;
	
	/**
	 * 학생 목록 검색
	 * 관리자
	 */
	@GetMapping("")
	public ResponseEntity<SearchResultVO<StudentVO.Detail>> getList(
			@ModelAttribute StudentVO.Search search,
			Model model) throws Exception {
		
		StudentVO.Search studentSearch = new StudentVO.Search();
		
		model.addAttribute("studentSearch", studentSearch);
		
		// 검색해요
		SearchResultVO<StudentVO.Detail> result = studentService.getList(search);
		
		// 학생 식별번호 암호화
		for(StudentVO.Detail student : result.getList()) {
			student.setEncryptedStudentNo(cryptoComponent.encrypt(String.valueOf(student.getStudentNo())));
			student.setStudentNo(0);
		}
		
		model.addAttribute("studentList", result);
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 학생 수정
	 * 관리자, 컨설턴트
	 */
	@PutMapping("{encryptedStudentNo}/update")
	public ResponseEntity<Void> putMethodName() {
		
		
		
		return ResponseEntity.ok().build();
	}

	/**
	 * 생기부 업로드
	 * 관리자/컨설턴트/학생
	 */
	@PostMapping("/{encryptedStudentNo}/record/upload")
	public ResponseEntity<String> recordUpload(
			@PathVariable String encryptedStudentNo,
			@RequestParam MultipartFile file,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = Integer.valueOf(cryptoComponent.decrypt(encryptedStudentNo));
		int memberNo = Integer.valueOf(cryptoComponent.decrypt(userDetails.getEncryptedMemberNo()));

		// MultipartFile을 FileDataVO로 변환
		FileDataVO fileData = FileDataVO.builder()
				.originalName(file.getOriginalFilename())
				.mime(file.getContentType())
				.size(file.getSize())
				.bytes(file.getBytes()).build();
		
		int groupNo = recordService.insertRecord(fileData, studentNo, memberNo);

		return ResponseEntity.ok(cryptoComponent.encrypt(String.valueOf(groupNo)));
	}
	
	/**
	 * 분석된 생기부 있나요
	 * 관리자/컨설턴트/학생
	 * 
	 * @param encryptedStudentNo
	 */
	@GetMapping("/record/status")
	public ResponseEntity<Void> getRecordStatus(
			@RequestParam String encryptedGroupNo) throws Exception{
		
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
			@PathVariable String encryptedStudentNo,
			@RequestParam MultipartFile file,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = Integer.valueOf(encryptedStudentNo);
		int memberNo = Integer.valueOf(cryptoComponent.decrypt(userDetails.getEncryptedMemberNo()));

		// MultipartFile을 FileDataVO로 변환
		FileDataVO fileData = FileDataVO.builder()
				.originalName(file.getOriginalFilename())
				.mime(file.getContentType())
				.size(file.getSize())
				.bytes(file.getBytes()).build();
		
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		int groupNo = mockService.insertMock(fileData, studentNo, memberNo);

		return ResponseEntity.ok(cryptoComponent.encrypt(String.valueOf(groupNo)));
	}
	
	/**
	 * 분석된 모의고사 성적표 있나요
	 * 관리자/컨설턴트/학생
	 * 
	 * @param encryptedStudentNo
	 */
	@GetMapping("/mock/status")
	public ResponseEntity<Void> getMockStatus(
			@RequestParam String encryptedGroupNo) throws Exception{
		
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
}
