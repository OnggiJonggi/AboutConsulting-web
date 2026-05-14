package com.axaboutconsulting.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

	@ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }
}
