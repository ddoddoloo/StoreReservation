package com.example.storereservation.global.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogIn implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString(); // uuid 생성
        request.setAttribute(LOG_ID, uuid); // afterCompletion에서 사용

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            log.info("[HandlerMethod] {}", hm.getMethod().getName());
        }

        log.info("[REQUEST] [URI: {}] [Handler: {}] [UUID: {}]", requestURI, handler, uuid);

        return true; // 요청을 처리하기 위해 계속 진행
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID); // preHandle에서 생성한 uuid

        if (ex != null) {
            log.error("[ERROR] [URI: {}] [UUID: {}] [Exception: {}]", requestURI, uuid, ex.getMessage());
        }

        log.info("[RESPONSE] [URI: {}] [UUID: {}]", requestURI, uuid);
    }
}
