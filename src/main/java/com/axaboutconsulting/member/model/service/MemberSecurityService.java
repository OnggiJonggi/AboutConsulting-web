package com.axaboutconsulting.member.model.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.axaboutconsulting.common.config.security.CustomUserDetails;
import com.axaboutconsulting.member.model.mapper.MemberMapper;
import com.axaboutconsulting.member.model.vo.Member;

/**
 * spring security에서 사용하는 로그인 서비스 로직
 * 사실상 로그인 전용
 */
@Service
public class MemberSecurityService implements UserDetailsService{
	
	private final MemberMapper memberMapper;
    public MemberSecurityService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

	// 로그인
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        
        Member.Detail memberDetail = memberMapper.selectMemberById(userId);
        
        if (memberDetail == null) {
            throw new UsernameNotFoundException("그런 사람 없다는데요");
        }

        return new CustomUserDetails(memberDetail);
	}
}
