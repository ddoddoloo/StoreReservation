package com.example.storereservation.domain.review.service;

import com.example.storereservation.domain.reservation.persist.ReservationEntity;
import com.example.storereservation.domain.reservation.persist.ReservationRepository;
import com.example.storereservation.domain.reservation.type.ReservationStatus;
import com.example.storereservation.domain.review.dto.AddReview;
import com.example.storereservation.domain.review.dto.EditReview;
import com.example.storereservation.domain.review.dto.ReviewDto;
import com.example.storereservation.domain.review.persist.ReviewEntity;
import com.example.storereservation.domain.review.persist.ReviewRepository;
import com.example.storereservation.domain.store.service.StoreService;
import com.example.storereservation.domain.user.persist.UserRepository;
import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import com.example.storereservation.global.type.PageConst;
import com.example.storereservation.global.type.ReviewSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final StoreService storeService;

    /**
     * 리뷰 쓰기
     * @param reservationId 예약 ID
     * @param userId 유저 ID
     * @param request 리뷰 작성 요청 정보
     * @return 작성된 리뷰 DTO
     */
    @Transactional
    public ReviewDto addReview(Long reservationId, String userId, AddReview.Request request) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new MyException(ErrorCode.RESERVATION_NOT_FOUND));

        validateReviewAvailable(reservation, userId);
        validateReviewDetail(request.getRating(), request.getText());

        ReviewEntity review = AddReview.Request.toEntity(request, reservation);
        ReviewEntity savedReview = reviewRepository.save(review);

        storeService.updateRatingForAddReview(ReviewDto.fromEntity(savedReview)); // 매장 리뷰 업데이트

        return ReviewDto.fromEntity(savedReview);
    }

    /**
     * 해당 리뷰를 쓸 권한이 있는지 검증
     * @param reservation 예약 엔티티
     * @param userId 유저 ID
     */
    private void validateReviewAvailable(ReservationEntity reservation, String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new MyException(ErrorCode.USER_NOT_FOUND);
        }
        if (!reservation.getUserId().equals(userId)) {
            throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
        }
        if (reviewRepository.existsByReservationId(reservation.getId())) {
            throw new MyException(ErrorCode.REVIEW_ALREADY_EXIST);
        }
        if (!reservation.getStatus().equals(ReservationStatus.USE_COMPLETE)) {
            throw new MyException(ErrorCode.REVIEW_NOT_AVAILABLE);
        }
    }

    /**
     * 리뷰의 별점 범위, 텍스트 길이 검증
     * @param rating 별점
     * @param text 리뷰 텍스트
     */
    private void validateReviewDetail(double rating, String text) {
        if (rating > 5 || rating < 0) {
            throw new MyException(ErrorCode.REVIEW_RATING_RANGE_ERROR);
        }
        if (text.length() > 200) {
            throw new MyException(ErrorCode.REVIEW_TEXT_TOO_LONG);
        }
    }

    /**
     * 리뷰 리스트 조회 by userId
     * @param userId 유저 ID
     * @param page 페이지 번호
     * @return 유저가 작성한 리뷰 리스트 페이지
     */
    public Page<ReviewDto> reviewListByUserId(String userId, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, PageConst.REVIEW_LIST_PAGE_SIZE);
        Page<ReviewEntity> findList = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageRequest);

        if (findList.getNumberOfElements() == 0) {
            throw new MyException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return findList.map(ReviewDto::fromEntity);
    }

    /**
     * 리뷰 리스트 조회 by storeName
     * @param storeName 상점 이름
     * @param sortType 정렬 타입
     * @param page 페이지 번호
     * @return 상점 리뷰 리스트 페이지
     */
    public Page<ReviewDto> reviewListByStoreName(String storeName, ReviewSortType sortType, Integer page) {
        PageRequest pageRequest = PageRequest.of(page, PageConst.REVIEW_LIST_PAGE_SIZE);
        Page<ReviewEntity> findList;

        if (sortType.equals(ReviewSortType.RATING_DESC)) {
            findList = reviewRepository.findByStoreNameOrderByRatingDesc(storeName, pageRequest);
        } else if (sortType.equals(ReviewSortType.RATING_ASC)) {
            findList = reviewRepository.findByStoreNameOrderByRatingAsc(storeName, pageRequest);
        } else {
            findList = reviewRepository.findByStoreNameOrderByCreatedAtDesc(storeName, pageRequest);
        }

        if (findList.getNumberOfElements() == 0) {
            throw new MyException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return findList.map(ReviewDto::fromEntity);
    }

    /**
     * 리뷰 수정
     * @param reviewId 리뷰 ID
     * @param userId 유저 ID
     * @param request 리뷰 수정 요청 정보
     * @return 수정된 리뷰 DTO
     */
    public ReviewDto editReview(Long reviewId, String userId, EditReview.Request request) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new MyException(ErrorCode.REVIEW_NOT_FOUND));

        double oldRating = reviewEntity.getRating();

        if (!reviewEntity.getUserId().equals(userId)) {
            throw new MyException(ErrorCode.NO_AUTHORITY_ERROR);
        }

        validateReviewDetail(request.getRating(), request.getText());

        reviewEntity.setRating(request.getRating());
        reviewEntity.setText(request.getText());
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        ReviewDto editedReview = ReviewDto.fromEntity(savedReview);

        storeService.updateRatingForEditReview(editedReview, oldRating);

        return editedReview;
    }
}
