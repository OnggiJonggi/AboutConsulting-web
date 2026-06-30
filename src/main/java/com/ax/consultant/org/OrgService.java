package com.ax.consultant.org;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ax.consultant.ConsultantVO;
import com.ax.global.common.SanitizeComponent;
import com.ax.global.common.SearchResultVO;
import com.ax.global.security.RoleEnum;
import com.ax.member.MemberMapper;
import com.ax.member.MemberRegexp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrgService {
	private final OrgMapper orgMapper;
	private final MemberMapper memberMapper;
	private final SanitizeComponent sanitizeComponent;

	/**
	 * 소속 목록 검색
	 */
	public SearchResultVO<OrgVO.Detail> getList(OrgVO.Search search) {
		
		// 검색어 소독
		search.setName(sanitizeComponent.searchKeyword(search.getName(), MemberRegexp.NAME_MAX_LENGTH));
		search.setConsultantName(sanitizeComponent.searchKeyword(search.getConsultantName(), MemberRegexp.NAME_MAX_LENGTH));
		search.setConsultantNickname(sanitizeComponent.searchKeyword(search.getConsultantNickname(), MemberRegexp.NAME_MAX_LENGTH));
		
		// 조회
		List<OrgVO.Detail> result = orgMapper.selectList(search);
		
		// 전체 수 조회
		int count = orgMapper.selectListTotalCount(search);
		
		// searchResult 생성
		SearchResultVO<OrgVO.Detail> searchResult = new SearchResultVO<OrgVO.Detail>(
				result, count, search.getPage());
		
		return searchResult;
	}

	/**
	 * 이 컨설턴트는 어디 소속인가요?
	 */
	public int isBelong(int memberNo) {
		
		int orgNo = orgMapper.selectIsBelog(memberNo);
		
		return orgNo;
	}

	/**
	 * 소속 기본정보 조회
	 */
	public OrgVO.Detail getDetail(int orgNo) {
		
		// 소속 조회
		OrgVO.Detail result = orgMapper.select(orgNo);
		if(result==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		// 컨설턴트 조회
		List<ConsultantVO.Detail> consultants = orgMapper.selectConsultant(orgNo);
		result.setConsultantDetail(consultants);
		
		return result;
	}

	/**
	 * 소속 이름 중복확인
	 */
	public void checkName(int orgNo, String name) {
		
		// 검색어 소독
		name = sanitizeComponent.searchKeywordNotLike(name, OrgRegexp.NAME_MAX_LENGTH);
		
		// 조회
		int isDupli = orgMapper.selectCheckName(orgNo, name);
		if(isDupli>0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	/**
	 * 소속 등록
	 * @return 소속 식별번호
	 */
	@Transactional
	public int register(OrgVO.Insert Insert) {
		
		// 조직 생성
		orgMapper.insertOrg(Insert);
		int orgNo = Insert.getOrgNo();
		if(orgNo == 0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 생성된 조직에 구성원 삽입
		int result1 = orgMapper.updateConsultantOrg(orgNo, Insert.getConsultantNos());
		if(result1 != Insert.getConsultantNos().size())
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 대표 컨설턴트는 권한 추가
		int result2 = memberMapper.insertRole(Insert.getLeaderNo(), RoleEnum.CONSULTANT_LEADER);
		if(result2 == 0)
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		return orgNo;
	}

	/**
	 * 소속 이름 변경
	 */
	public void updateName(int orgNo, String name) {
		
		// 중복 확인
		int isDupli = orgMapper.selectCheckName(orgNo, name);
		if(isDupli>0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		// 변경
		int result = orgMapper.updateName(orgNo, name);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 소속 상태값 변경
	 */
	public void updateStatus(int orgNo, OrgStatusEnum status) {
		int result = orgMapper.updateStatus(orgNo, status);
		if(result==0) throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 두 컨설턴트가 같은 소속인가요?
	 * @param : 두 컨설턴트의 식별번호
	 */
	public boolean isSameOrg(int no1, int no2) {
		
		int result = orgMapper.selectIsSameOrg(no1, no2);
		
		if(result == 1) return true;
		else return false;
	}

	/**
	 * 소속 - 컨설턴트 배정
	 */
	@Transactional
	public void updateCharged(int orgNo, int leaderNo, Set<Integer> conNos) {

		// 모든 구성원 삭제
		orgMapper.updateConsultantOrgForNull(orgNo);
		
		// 구성원 삽입
		int result1 = orgMapper.updateConsultantOrg(orgNo, conNos);
		if(result1 != conNos.size())
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 기존 대표 컨설턴트의 컨설턴트 식별번호 추출
		int oldLeaderNo = orgMapper.selectLeaderNo(orgNo);
		if(oldLeaderNo == 0)
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		// 대표 컨설턴트 변경
		if(oldLeaderNo != leaderNo) {
			
			// CONSULTANT_ORG 테이블 수정
			int result2 = orgMapper.updateOrgLeader(orgNo, leaderNo); 
			if(result2 == 0)
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
			// 새로운 대표 컨설턴트 권한 추가
			int result3 = memberMapper.insertRole(leaderNo, RoleEnum.CONSULTANT_LEADER);
			if(result3 == 0)
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
			// 기존 대표 모든 권한 삭제
			int result4 = memberMapper.deleteRole(oldLeaderNo);
			if(result4 == 0)
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
			// 기존 대표 컨설턴트 권한(CONSULTANT) 추가
			int result5 = memberMapper.insertRole(oldLeaderNo, RoleEnum.CONSULTANT);
			if(result5 == 0)
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			
		} else {
			// 대표 미 변경 -> 다시 대표로 올려놓기
			int result6 = orgMapper.updateOrgLeader(orgNo, oldLeaderNo);
			if(result6 == 0)
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
