package com.ax.member;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ax.global.security.CryptoComponent;
import com.ax.global.security.CustomUserDetails;
import com.ax.global.security.RoleMapper;

import lombok.RequiredArgsConstructor;

/**
 * spring security에서 사용하는 로그인 서비스 로직
 * 사실상 로그인 전용
 */
@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService{
	private final MemberMapper memberMapper;
	private final CryptoComponent cryptoComponent;
	private final RoleMapper roleMapper;

	// 로그인
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        
        MemberVO.Detail memberDetail = memberMapper.selectMemberById(userId);
        
        if (memberDetail == null) {
            throw new UsernameNotFoundException("그런 사람 없다는데요");
        }
        
        memberDetail.setRole(roleMapper.selectMemberRole(memberDetail.getMemberNo()));

        // memberNo암호화
        try {
			memberDetail.setEncryptedMemberNo(cryptoComponent.encrypt(String.valueOf(memberDetail.getMemberNo())));
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException(userId);
		}
        memberDetail.setMemberNo(0);
        
        return new CustomUserDetails(memberDetail);
	}
}
