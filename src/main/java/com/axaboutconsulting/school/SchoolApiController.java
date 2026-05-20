package com.axaboutconsulting.school;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.axaboutconsulting.api.ApiSchoolService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/school")
public class SchoolApiController {
	@Autowired
	private SchoolService service;
	@Autowired
	private ApiSchoolService apiService;

	/**
	 * 학교 데이터 db조회
	 * 관리자/컨설턴트
	 * 
	 * @param schoolName
	 * @return List<schoolDetail>
	 */
	@GetMapping("/search")
	public ResponseEntity<List<SchoolVO.Detail>> search(String schoolName) {
		return ResponseEntity.ok(service.search(schoolName));
	}

	/**
	 * 학교 데이터 공공데이터 조회
	 * 관리자/컨설턴트
	 * 
	 * @param schoolName
	 * @return List<schoolDetail>
	 */
	@GetMapping("/search/open")
	public ResponseEntity<List<SchoolVO.Detail>> search(String schoolName, HttpSession session) throws Exception {

		List<SchoolVO.Detail> result = apiService.search(schoolName);

		// 세션에 캐싱해두고 db에 없는 학교 등록할 때 재사용
		session.setAttribute("schoolSearchResult", result);
		session.setMaxInactiveInterval(300);

		return ResponseEntity.ok(result);
	}

	/**
	 * 학교 등록
	 * 관리자/컨설턴트
	 * 
	 * @param schoolCode
	 * @param session
	 */
	@PostMapping("/register")
	public ResponseEntity<Void> registor(String schoolCode, HttpSession session) {
		List<SchoolVO.Detail> cachedList = (List<SchoolVO.Detail>) session.getAttribute("schoolSearchResult");

		// 세션 만료
		if (cachedList == null)
			return ResponseEntity.badRequest().build();

		// schoolCode로 대상 schoolDetail찾기
		SchoolVO.Detail target = cachedList
				.stream() // 컬렉션 반복문
				.filter(s -> s.getSchoolCode().equals(schoolCode)) // 찾아라
				.findFirst() // 첫 번째 반환
				.orElse(null); // 없으면 말고

		// schoolDetail 없으면 가세요라
		if(target == null)
			return ResponseEntity.notFound().build();
		
		try {
			service.registor(target);
		} catch (DuplicateKeyException e) {
			// 중복된 경우
			session.removeAttribute("schoolSearchResult");
			return ResponseEntity.status(409).build();
		}

		// 세션 파괴
		session.removeAttribute("schoolSearchResult");
		
		return ResponseEntity.ok().build();
	}

}
