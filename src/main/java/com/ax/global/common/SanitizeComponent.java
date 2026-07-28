package com.ax.global.common;

import org.springframework.stereotype.Component;

/**
 * 사용자 입력값 소독
 */
@Component
public class SanitizeComponent {

	/**
	 * 검색어 소독
	 * 
	 * 클라이언트에서 DB로 LIKE문법을 사용하는 조회를 할 때 사용
	 * 
	 * 1. trim()
	 * 2. 최대 길이 제한 - 엄청난 길이의 쿼리스트링으로 DB괴롭히기 멈춰!
	 * 3. LIKE예약어(%,_) 이스케이프
	 * 
	 * @param 검색 문자열
	 * @param 최대 허용 길이(정규식 저장소에서 얻어냄)
	 * @return 소독된 문자열
	 */
	public String searchKeyword(String keyword, int maxLength) {
		
		// 없으면 가라
		if (keyword == null) return null;

		// trim()
		keyword = keyword.trim();

		// 길면 잘라
		if (keyword.length() > maxLength)
			keyword = keyword.substring(0, maxLength);

		// 이스케이프 문자 : '/'
		keyword = keyword
				.replace("/", "//")
				.replace("%", "/%")
				.replace("_", "/_");

		return keyword;
	}
	
	
	/**
	 * 검색어 소독
	 * 
	 * 클라이언트에서 DB로 LIKE문법을 사용하지 않는 조회를 할 때 사용
	 * 
	 * 1. trim()
	 * 2. 최대 길이 제한
	 * 
	 * @param 검색 문자열
	 * @param 최대 허용 길이(정규식 저장소에서 얻어냄)
	 * @return 소독된 문자열
	 */
	public String searchKeywordNotLike(String keyword, int maxLength) {
		
		// 없으면 가라
		if (keyword == null) return null;
		
		// trim()
		keyword = keyword.trim();
		
		// 길면 잘라
		if (keyword.length() > maxLength)
			keyword = keyword.substring(0, maxLength);
		
		return keyword;
	}
}
