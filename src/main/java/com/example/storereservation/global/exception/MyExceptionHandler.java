package com.example.storereservation.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    /**
     * MyException 예외 처리 핸들러
     * @param e 발생한 MyException 예외
     * @return 에러 응답 객체
     */
    @ExceptionHandler(MyException.class)
    protected ResponseEntity<ErrorResponse> handleMyException(MyException e) {
        log.error("MyException 발생: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatusCode()));
    }
}
