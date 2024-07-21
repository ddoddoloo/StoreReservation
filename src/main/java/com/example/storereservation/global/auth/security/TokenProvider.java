package com.example.storereservation.global.auth.security;

import com.example.storereservation.global.auth.service.AuthService;
import com.example.storereservation.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1시간
    private final AuthService authService;

    /**
     * 토큰 생성 (발급)
     * @param username 사용자 이름
     * @param roles 사용자 역할 목록
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expiredDate) // 토큰 만료시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 시크릿 키
                .compact();
    }

    /**
     * 토큰으로 사용자 이름 찾기
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검사
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            if (!StringUtils.hasText(token)) return false;
            Claims claims = this.parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            log.error("토큰 유효성 검사 실패: {}", e.getMessage());
            throw new JwtException(e.getMessage());
        }
    }

    /**
     * 토큰 파싱
     * @param token JWT 토큰
     * @return 파싱된 클레임
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("토큰 만료됨: {}", e.getMessage());
            throw new JwtException(ErrorCode.TOKEN_TIME_OUT.getDescription());
        } catch (SignatureException e) {
            log.error("토큰 서명 불일치: {}", e.getMessage());
            throw new JwtException(ErrorCode.JWT_TOKEN_WRONG_TYPE.getDescription());
        }
    }

    /**
     * 인증 객체 생성
     * @param token JWT 토큰
     * @return 인증 객체
     */
    public Authentication getAuthentication(String token) {
        String username = this.getUsername(token);
        UserDetails userDetails = authService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
