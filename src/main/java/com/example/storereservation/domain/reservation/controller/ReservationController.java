package com.example.storereservation.domain.reservation.controller;

import com.example.storereservation.domain.reservation.dto.ReservationDto;
import com.example.storereservation.domain.reservation.service.ReservationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 예약 상세 정보 보기
     * - @AuthenticationPrincipal로 로그인 된 유저 정보를 받아서 유저 또는 파트너에게 정보 제공
     * @param reservationId : 예약 ID
     * @param userDetails : 로그인 정보
     * @return ResponseEntity에 예약 상세 정보가 담겨 반환
     */
    @ApiOperation(value = "예약 상세 정보", notes = "예약 ID를 사용하여 예약 상세 정보를 조회합니다.")
    @GetMapping("/detail/{reservationId}")
    public ResponseEntity<ReservationDto> reservationDetail(@PathVariable Long reservationId,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        ReservationDto reservationDto = reservationService.reservationDetail(reservationId, userDetails.getUsername());
        return ResponseEntity.ok(reservationDto);
    }
}
