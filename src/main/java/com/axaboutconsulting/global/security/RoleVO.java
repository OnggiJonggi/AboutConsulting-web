package com.axaboutconsulting.global.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class RoleVO {
	
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class UrlAccessCheck{
		int memberNo;
		String pattern;
		String httpMethod;
	}
}
