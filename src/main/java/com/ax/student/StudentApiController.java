package com.ax.student;

import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.ConsultantService;
import com.ax.global.common.SearchResultVO;
import com.ax.global.file.FileDataVO;
import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleEnum;
import com.ax.student.mock.MockService;
import com.ax.student.mock.MockStatusEnum;
import com.ax.student.record.RecordService;
import com.ax.student.record.RecordStatusEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@Slf4j
public class StudentApiController {
	private final StudentService studentService;
	private final ConsultantService consultantService;
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
			student.setEncStudentNo(cryptoComponent.encrypt(student.getStudentNo()));
			student.setStudentNo(0);
		}
		
		model.addAttribute("studentList", result);
		
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 학생 수정
	 * 관리자, 컨설턴트 : 담당, 학생 : 본인
	 * 
	 * TODO: 학생 본인 확인
	 */
	@PutMapping("{encStudentNo}")
	public ResponseEntity<Void> updateStudent(
			@ModelAttribute @Valid StudentVO.Insert student,
			@PathVariable String encStudentNo,
			@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception{
		
		int studentNo = cryptoComponent.decrypt(encStudentNo);
		
		// 컨설턴트라면 담당 학생인지 확인
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.CONSULTANT.getPrefix()))) {
			int consultantNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
			
			boolean inCharge = consultantService.isInCharge(consultantNo, studentNo);
			if(inCharge) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		
		
		student.setStudentNo(studentNo);
		studentService.updateStudent(student);
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 학생 상태값 수정
	 * 관리자, 컨설턴트 : 담당
	 */
	@PutMapping("{encStudentNo}/status")
	public ResponseEntity<Void> deleteStudent(
			@PathVariable String encStudentNo,
			@RequestParam StudentStatusEnum status,
			@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception{
		
		int studentNo = cryptoComponent.decrypt(encStudentNo);
		
		// 컨설턴트라면 담당 학생인지 확인
		if(userDetails.getAuthorities().stream()
		        .anyMatch(a -> a.getAuthority().equals(RoleEnum.CONSULTANT.getPrefix()))) {
			int consultantNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());
			
			boolean inCharge = consultantService.isInCharge(consultantNo, studentNo);
			if(inCharge) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		
		studentService.updateStatus(studentNo, status);
		
		return ResponseEntity.ok().build();
	}
	

	/**
	 * 생기부 업로드
	 * 관리자, 컨설턴트, 학생
	 * 
	 * 1. 파일 확장자, MIME가 pdf인지 확인
	 * 2. 파일을 service전달용 객체에 역직렬화
	 * 3. 서비스 메서드 작동
	 * 4. RECORD_ANALYSIS_GROUP테이블의 GROUP_NO을 암호화해 반환
	 */
	@PostMapping("{encStudentNo}/record/upload")
	public ResponseEntity<String> recordUpload(
			@PathVariable String encStudentNo,
			@RequestParam MultipartFile file,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = cryptoComponent.decrypt(encStudentNo);
		int memberNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());

		// MultipartFile을 FileDataVO로 변환
		FileDataVO fileData = FileDataVO.builder()
				.originalName(file.getOriginalFilename())
				.mime(file.getContentType())
				.size(file.getSize())
				.bytes(file.getBytes()).build();
		
		int groupNo = recordService.insertRecord(fileData, studentNo, memberNo);

		return ResponseEntity.ok(cryptoComponent.encrypt(groupNo));
	}
	
	/**
	 * 분석된 생기부 있나요
	 * 관리자, 컨설턴트, 학생
	 * 
	 * 1. GROUP_NO 복호화
	 * 2. RECORD_ANALYSIS_GROUP 테이블의 STATUS조회
	 */
	@GetMapping("record/status")
	public ResponseEntity<Void> getRecordStatus(
			@RequestParam String encGroupNo) throws Exception{
		
		int groupNo = cryptoComponent.decrypt(encGroupNo);

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
	 * 관리자, 컨설턴트, 학생
	 * 
	 * 1. 파일 확장자, MIME가 pdf인지 확인
	 * 2. 파일을 service전달용 객체에 역직렬화
	 * 3. 서비스 메서드 작동
	 * 4. MOCK_GROUP테이블의 GROUP_NO을 암호화해 반환
	 */
	@PostMapping("{encStudentNo}/mock/upload")
	public ResponseEntity<String> mockUpload(
			@PathVariable String encStudentNo,
			@RequestParam MultipartFile file,
			@AuthenticationPrincipal CustomUserDetails userDetails,
			Model model) throws Exception{
		
		// 업로드한 파일이 pdf가 맞는지 확장자, MIME검사
		if(!file.getOriginalFilename().endsWith(".pdf")
				|| !file.getContentType().equals("application/pdf"))
			return ResponseEntity.badRequest().build();
		
		int studentNo = Integer.valueOf(encStudentNo);
		int memberNo = cryptoComponent.decrypt(userDetails.getEncMemberNo());

		// MultipartFile을 FileDataVO로 변환
		FileDataVO fileData = FileDataVO.builder()
				.originalName(file.getOriginalFilename())
				.mime(file.getContentType())
				.size(file.getSize())
				.bytes(file.getBytes()).build();
		
		// 모의고사 묶음 + 비동기 요청 작업 상태값 생성
		int groupNo = mockService.insertMock(fileData, studentNo, memberNo);

		return ResponseEntity.ok(cryptoComponent.encrypt(groupNo));
	}
	
	/**
	 * 분석된 모의고사 성적표 있나요
	 * 관리자, 컨설턴트, 학생
	 * 
	 * 1. GROUP_NO 복호화
	 * 2. MOCK_GROUP 테이블의 STATUS조회
	 */
	@GetMapping("mock/status")
	public ResponseEntity<Void> getMockStatus(
			@RequestParam String encGroupNo) throws Exception{
		
		int groupNo = cryptoComponent.decrypt(encGroupNo);

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
