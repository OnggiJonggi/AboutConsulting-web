package com.axaboutconsulting.global.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 검색 결과 및 페이징 필드 반환용 객체
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SearchResultVO<T> {

	private List<T> list; // 검색 결과

	private int totalCount; // 전체 검색 수
	private int totalPage; // 전체 페이지 수
	private int startPage; // 페이지 블록 시작 번호
	private int endPage; // 페이지 블록 끝 번호
	private boolean hasPrev; // 다음 블록 있나요
	private boolean hasNext; // 이전 블록 있나요

	/**
	 * 필드 자동완성 생성자
	 * 
	 * @param list       검색 결과
	 * @param totalCount 총 데이터 수
	 * @param page       요청 페이지 번호
	 */
	public SearchResultVO(List<T> list, int totalCount, int page) {
		int pageSize = PagingConstant.PAGE_SIZE;
		int blockSize = PagingConstant.BLOCK_SIZE;
		
		this.list = list;

		// totlaCount가 0일때 엣지 예외 처리
		if (totalCount == 0) {
			this.totalPage = 1;
			this.endPage = 1;
			this.startPage = 1;
			page = 1;
			this.hasPrev = false;
			this.hasNext = false;
			return;
		}

		this.totalCount = totalCount;

		this.totalPage = (int) Math.ceil((double) totalCount / pageSize);

		if (this.totalPage < page)
			page = this.totalPage;

		this.endPage = (int) Math.ceil((double) page / blockSize) * blockSize;

		this.startPage = this.endPage - blockSize + 1;

		if (this.endPage > this.totalPage)
			this.endPage = this.totalPage;
		if (this.startPage < 1)
			this.startPage = 1;

		this.hasPrev = this.startPage > 1;

		this.hasNext = this.endPage < this.totalPage;
	}

}
