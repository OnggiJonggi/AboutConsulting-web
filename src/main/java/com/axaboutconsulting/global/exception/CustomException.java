package com.axaboutconsulting.global.exception;

import lombok.Getter;

/**
 * 이쁘고 깔끔하게 커스텀 예외 처리를 할꺼에요
 */
@Getter
public class CustomException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final ErrorCodeEnum errorCode;
	
	public CustomException(ErrorCodeEnum errorCode) {
		
		// RuntimeException에 메시지 넘겨주기
        super(errorCode.getMessage());
        
        this.errorCode = errorCode;
    }
}
