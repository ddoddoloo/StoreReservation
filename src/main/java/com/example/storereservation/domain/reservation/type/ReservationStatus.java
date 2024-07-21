package com.example.storereservation.domain.reservation.type;

import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import org.springframework.util.StringUtils;

public enum ReservationStatus {
    /**
     * 예약 요청 중
     */
    REQUESTING,

    /**
     * 예약 거절 상태
     */
    REFUSED,

    /**
     * 예약 승인 상태
     */
    CONFIRM,

    /**
     * 도착 확인
     */
    ARRIVED,

    /**
     * 이용 완료
     */
    USE_COMPLETE,

    /**
     * 예약 승인 후 이용하지 않음(no-show)
     */
    NO_SHOW,

    /**
     * 테스트용
     */
    T_E_S_T;

    /**
     * 주어진 문자열을 기반으로 ReservationStatus를 반환합니다.
     * @param status 예약 상태 문자열
     * @return 해당 문자열에 해당하는 ReservationStatus
     * @throws MyException 예약 상태 코드가 없거나 유효하지 않을 경우 예외 발생
     */
    public static ReservationStatus of(String status) {
        if (!StringUtils.hasText(status)) {
            throw new MyException(ErrorCode.RESERVATION_STATUS_CODE_REQUIRED);
        }

        String upperCaseStatus = status.toUpperCase();
        for (ReservationStatus rs : ReservationStatus.values()) {
            if (rs.name().equals(upperCaseStatus)) {
                return rs;
            }
        }

        throw new MyException(ErrorCode.RESERVATION_STATUS_CODE_ILLEGAL_ARGUMENT);
    }
}
