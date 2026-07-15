package com.ax.school;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/school")
@RequiredArgsConstructor
public class SchoolApiController {
	private final SchoolService schoolService;
	private final SchoolApiService apiService;

	/**
	 * 학교 데이터 db조회
	 * 관리자, 컨설턴트
	 * 
	 * DB에 학교 데이터가 있는지 검사
	 */
	@GetMapping("")
	public ResponseEntity<List<SchoolVO.Detail>> getSchoolListFromDB(
			@RequestParam String schoolName) {
		return ResponseEntity.ok(schoolService.getList(schoolName));
	}

	/**
	 * 나이스 학교 공공데이터 조회
	 * 관리자, 컨설턴트
	 *
	 * 검색 결과를 세션에 저장해두고 검색 결과를 선택하면 세션에서 불러와 사용
	 */
	@GetMapping("high")
	public ResponseEntity<List<SchoolVO.Detail>> getHighSchoolList(
			@RequestParam String schoolName,
			HttpSession session) throws Exception {

		List<SchoolVO.Detail> result = apiService.getSchool(schoolName);

		// 세션에 캐싱해두고 db에 없는 학교 등록할 때 재사용
		session.setAttribute("schoolSearchResult", result);
		session.setMaxInactiveInterval(300);

		return ResponseEntity.ok(result);
	}
	
	/**
	 * (미사용)
	 * 대학교 데이터 공공데이터 조회
	 * 관리자, 컨설턴트
	 */
//	@GetMapping("univ")
//	public ResponseEntity<List<SchoolVO.UnivDetail>> getUnivList(
//			@RequestParam String univ,
//			@RequestParam String major,
//			HttpSession session) throws Exception {
//		
//		List<SchoolVO.UnivDetail> result = apiService.getUniv(univ, major);
//		
//		return ResponseEntity.ok(result);
//	}

	/**
	 * 학교 등록
	 * 관리자, 컨설턴트
	 * 
	 * 세션에 저장된 나이스 학교 공공데이터 조회 결과를 DB에 넣기
	 * 동일한 학교가 이미 DB에 있으면 오류
	 */
	@PostMapping("register")
	public ResponseEntity<Void> registor(
			@RequestParam String schoolCode,
			HttpSession session) {
		
		// 세션에 저장된 학교 목록 불러오기
		@SuppressWarnings("unchecked") List<SchoolVO.Detail> cachedList
			= (List<SchoolVO.Detail>) session.getAttribute("schoolSearchResult");

		// 세션 만료면 가세요라
		if (cachedList == null) return ResponseEntity.badRequest().build();
		
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
			schoolService.insertOne(target);
		} catch (DuplicateKeyException e) {
			// 이미 학교가 있을 경우
			session.removeAttribute("schoolSearchResult");
			return ResponseEntity.status(409).build();
		}

		// 세션 파괴
		session.removeAttribute("schoolSearchResult");
		
		return ResponseEntity.ok().build();
	}

}
