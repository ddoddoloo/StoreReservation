package com.example.storereservation.domain.user.controller;

import com.example.storereservation.domain.user.dto.RegisterUser;
import com.example.storereservation.domain.user.dto.UserDto;
import com.example.storereservation.domain.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 유저 회원가입
     * @param request 회원가입 요청 정보
     * @return 회원가입된 유저 정보
     */
    @ApiOperation("유저 회원가입")
    @PostMapping("/user/register")
    public ResponseEntity<RegisterUser.Response> registerUser(@RequestBody RegisterUser.Request request) {
        UserDto registeredUser = userService.register(request);
        return ResponseEntity.ok(RegisterUser.Response.fromDto(registeredUser));
    }
}
