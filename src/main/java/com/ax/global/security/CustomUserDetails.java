package com.ax.global.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ax.member.MemberVO;

import lombok.RequiredArgsConstructor;

//spring security에서 사용하는 UserDetails 수정
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
    private final MemberVO.Detail member;

    //사용자 권한 식별 장치
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole() != null) {
            for (String role : member.getRole()) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }
        return authorities;
    }

    // 비밀번호 검증
    @Override
    public String getPassword() {
        return member.getUserPwd();
    }

    // 아이디 검증
    @Override
    public String getUsername() {
        return member.getUserId();
    }
    
    // 이름 꺼내쓰기. 원 UserDetails 클래스에는 없는 기능.
    public String getOriginName() {
        return member.getName();
    }
    
    // 별명 꺼내쓰기
    public String getNickname() {
    	return member.getNickname();
    }
    
    // 암호화된 회원번호 꺼내기
    public String getEncMemberNo() {
    	return member.getEncMemberNo();
    }
    
    /*
     * 이 아래부터는 미사용
     */
    
    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정 잠김 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }
}
