package com.example.storereservation.domain.reservation.service;

import com.example.storereservation.domain.reservation.dto.MakeReservation;
import com.example.storereservation.domain.reservation.dto.ReservationDto;
import com.example.storereservation.domain.reservation.persist.ReservationEntity;
import com.example.storereservation.domain.reservation.persist.ReservationRepository;
import com.example.storereservation.domain.reservation.type.ReservationStatus;
import com.example.storereservation.domain.store.persist.StoreEntity;
import com.example.storereservation.domain.store.persist.StoreRepository;
import com.example.storereservation.domain.user.persist.UserEntity;
import com.example.storereservation.domain.user.persist.UserRepository;
import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import com.example.storereservation.global.type.PageConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /**
     * 유저 - 매장 예약
     * @param request 예약 요청 정보
     * @return 예약 상세 정보
     */
    public ReservationDto makeReservation(MakeReservation.Request request) {
        ReservationEntity reservation = createReservationEntity(request);
        ReservationEntity saved = reservationRepository.save(reservation);
        log.info("reservation id : {}", saved.getId());
        return ReservationDto.fromEntity(saved);
    }

    /**
     * 매장 예약 Request를 바탕으로 ReservationEntity 생성
     */
    private ReservationEntity createReservationEntity(MakeReservation.Request request) {
        UserEntity user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new MyException(ErrorCode.USER_NOT_FOUND));
        StoreEntity store = storeRepository.findByStoreName(request.getStoreName())
                .orElseThrow(() -> new MyException(ErrorCode.STORE_NOT_FOUND));
        LocalDateTime reservationTime = LocalDateTime.of(request.getDate(), request.getTime());

        return ReservationEntity.builder()
                .userId(user.getUserId())
                .phone(user.getPhone())
                .partnerId(store.getPartnerId())
                .storeName(store.getStoreName())
                .people(request.getPeople())
                .status(ReservationStatus.REQUESTING)
                .statusUpdatedAt(LocalDateTime.now())
                .time(reservationTime)
                .build();
    }

    /**
     * 유저/파트너 - 예약 상세 정보
     */
    public ReservationDto reservationDetail(Long id, String username) {
        ReservationEntity reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new MyException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!validateReservationAccessAuthority(username, reservation)) {
            throw new MyException(ErrorCode.ACCESS_DENIED);
        }

        return ReservationDto.fromEntity(reservation);
    }

    /**
     * userDetails의 username이 reservation의 userId 또는 partnerId와 일치하는지 확인
     */
    private boolean validateReservationAccessAuthority(String username, ReservationEntity reservation) {
        if (reservation.getUserId().equals(username)) {
            log.info("UserID : {}, 예약 내역 확인", username);
            return true;
        } else if (reservation.getPartnerId().equals(username)) {
            log.info("PartnerId : {}, 예약 내역 확인", username);
            return true;
        }
        return false;
    }

    /**
     * 파트너 - partner ID로 예약 내역 확인
     * @param partnerId 파트너 ID
     * @param page 페이지 번호
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForPartner(String partnerId, Integer page) {
        Page<ReservationEntity> reservations = reservationRepository.findByPartnerIdOrderByTimeDesc(
                partnerId, PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 파트너 - partner ID와 ReservationStatus로 내역 확인
     * @param partnerId 파트너 ID
     * @param status 예약 상태
     * @param page 페이지 번호
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForPartnerByStatus(String partnerId, ReservationStatus status, Integer page) {
        Page<ReservationEntity> reservations = reservationRepository.findByPartnerIdAndStatusOrderByTime(
                partnerId, status, PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 파트너 - partner ID와 예약 날짜로 내역 확인
     * @param partnerId 파트너 ID
     * @param date 예약 날짜
     * @param page 페이지 번호
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForPartnerByDate(String partnerId, LocalDate date, Integer page) {
        Page<ReservationEntity> reservations = reservationRepository.findByPartnerIdAndTimeBetweenOrderByTime(
                partnerId, LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX),
                PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 파트너 - partner ID와 예약 상태(status), 예약 날짜(time)로 내역 확인
     * @param partnerId 파트너 ID
     * @param status 예약 상태
     * @param date 예약 날짜
     * @param page 페이지 번호
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForPartnerByStatusAndDate(String partnerId, ReservationStatus status, LocalDate date, Integer page) {
        Page<ReservationEntity> reservations = reservationRepository.findByPartnerIdAndStatusAndTimeBetweenOrderByTime(
                partnerId, status, LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX),
                PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 파트너 - 예약 상태 변경
     * @param partnerId 파트너 ID
     * @param reservationId 예약 ID
     * @param status 예약 상태
     */
    public void changeReservationStatus(String partnerId, Long reservationId, ReservationStatus status) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new MyException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getPartnerId().equals(partnerId)) {
            throw new MyException(ErrorCode.RESERVATION_UPDATE_AUTH_FAIL);
        }
        reservation.setStatus(status);
        reservation.setStatusUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
    }

    /**
     * 유저 - user ID로 예약 내역 확인
     * @param userId 유저 ID
     * @param page 페이지 번호
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForUser(String userId, Integer page) {
        Page<ReservationEntity> reservations = reservationRepository.findByUserIdOrderByTimeDesc(
                userId, PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 유저 - userId와 ReservationStatus로 내역 확인
     * @param userId 유저 ID
     * @param page 페이지 번호
     * @param status 예약 상태
     * @return 예약 내역 페이지
     */
    public Page<ReservationDto> listForUserByStatus(String userId, Integer page, ReservationStatus status) {
        Page<ReservationEntity> reservations = reservationRepository.findByUserIdAndStatusOrderByTime(
                userId, status, PageRequest.of(page, PageConst.RESERVATION_LIST_PAGE_SIZE));

        if (reservations.getSize() == 0) {
            throw new MyException(ErrorCode.RESERVATION_IS_ZERO);
        }
        return reservations.map(ReservationDto::fromEntity);
    }

    /**
     * 도착 확인
     * @param reservationId 예약 ID
     * @param inputPhoneNumberLast4 전화번호 마지막 4자리
     * @return 예약 상세 정보
     */
    public ReservationDto arrivedCheck(Long reservationId, String inputPhoneNumberLast4) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new MyException(ErrorCode.RESERVATION_NOT_FOUND));
        validateArrivedCheck(reservation, inputPhoneNumberLast4);
        reservation.setStatus(ReservationStatus.ARRIVED);
        reservation.setStatusUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);
        return ReservationDto.fromEntity(reservation);
    }

    /**
     * 도착 확인 validation
     * @param reservation 예약 엔티티
     * @param inputPhoneNumberLast4 입력된 전화번호 마지막 4자리
     */
    private void validateArrivedCheck(ReservationEntity reservation, String inputPhoneNumberLast4) {
        String rightPhoneNumberLast4 = reservation.getPhone().substring(7);

        if (!rightPhoneNumberLast4.equals(inputPhoneNumberLast4)) {
            throw new MyException(ErrorCode.RESERVATION_PHONE_NUMBER_INCORRECT);
        } else if (!reservation.getStatus().equals(ReservationStatus.CONFIRM)) {
            throw new MyException(ErrorCode.RESERVATION_STATUS_CHECK_ERROR);
        } else if (LocalDateTime.now().isAfter(reservation.getTime().minusMinutes(10L))) {
            throw new MyException(ErrorCode.RESERVATION_TIME_CHECK_ERROR);
            //(현재시간) > (예약 시간 - 10분) => 10분 전에 도착하지 못함.
        }
    }
}
