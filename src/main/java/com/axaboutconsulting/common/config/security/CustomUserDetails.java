package com.axaboutconsulting.common.config.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.axaboutconsulting.member.model.vo.Member;
import com.axaboutconsulting.member.model.vo.Member.Detail;

//spring security에서 사용하는 UserDetails 수정
public class CustomUserDetails implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
    private final Member.Detail member;
    
    public CustomUserDetails(Detail memberDetail) {
        this.member = memberDetail;
    }

    //사용자 다중 권한 식별 장치
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	 return Collections.emptyList();
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
    public String getNickName() {
    	return member.getNickName();
    }
    
    
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
