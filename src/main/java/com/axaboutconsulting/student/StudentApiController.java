package com.axaboutconsulting.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.global.common.SearchResultVO;

@RestController
@RequestMapping("/student")
public class StudentApiController {
	@Autowired
	public StudentService service;
	
	/**
	 * 학생 목록 조회
	 * 컨설턴트/관리자
	 * @param studentSearch
	 * @return 200 + SearchResultVO
	 * @throws Exception 
	 */
	@PostMapping("/list")
	public ResponseEntity<SearchResultVO<StudentVO.Detail>> list(StudentVO.Search studentSearch) throws Exception{
		return ResponseEntity.ok(service.getList(studentSearch));
	}

}
