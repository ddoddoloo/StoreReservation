package com.example.storereservation.global.auth.controller;

import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SecurityController {

    /**
     * ACCESS_DENIED 오류 발생 시
     * 로그인은 되어있으나 접근 권한이 없을 때
     */
    @ApiOperation(value = "ACCESS_DENIED 오류 발생 시", notes = "로그인은 되어있으나 접근 권한이 없을 때")
    @GetMapping("/exception/auth-denied")
    public void accessDenied() {
        log.info("ACCESS_DENIED - SecurityController");
        throw new MyException(ErrorCode.ACCESS_DENIED);
    }

    /**
     * LOGIN_REQUIRED 오류 발생 시
     * 로그인 되지 않은 상태일 때
     */
    @ApiOperation(value = "LOGIN_REQUIRED 오류 발생 시", notes = "로그인 되지 않은 상태일 때")
    @GetMapping("/exception/unauthorized")
    public void unauthorized() {
        log.info("LOGIN_REQUIRED - SecurityController");
        throw new MyException(ErrorCode.LOGIN_REQUIRED);
    }
}
