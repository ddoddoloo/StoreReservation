package com.example.storereservation.domain.user.service;

import com.example.storereservation.domain.user.dto.RegisterUser;
import com.example.storereservation.domain.user.dto.UserDto;
import com.example.storereservation.domain.user.persist.UserEntity;
import com.example.storereservation.domain.user.persist.UserRepository;
import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import com.example.storereservation.global.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 회원가입
     * @param request 회원가입 요청 정보
     * @return 등록된 유저 정보
     */
    public UserDto register(RegisterUser.Request request) {
        validatePassword(request.getPassword(), request.getPasswordCheck());

        if (userRepository.existsByUserId(request.getUserId())) {
            throw new MyException(ErrorCode.DUPLICATED_ID);
        }

        request.setPassword(PasswordUtils.encPassword(request.getPassword()));

        UserEntity savedMember = userRepository.save(RegisterUser.Request.toEntity(request));

        log.info("User register complete: {}", savedMember);

        return UserDto.fromEntity(savedMember);
    }

    /**
     * 비밀번호 검증
     * @param password 비밀번호
     * @param passwordCheck 비밀번호 확인
     */
    private void validatePassword(String password, String passwordCheck) {
        if (!PasswordUtils.validatePlainTextPassword(password, passwordCheck)) {
            throw new MyException(ErrorCode.PASSWORD_CHECK_INCORRECT);
        }
    }
}
