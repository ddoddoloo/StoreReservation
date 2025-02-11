package com.example.storereservation.domain.store.controller;

import com.example.storereservation.domain.review.ReviewListInput;
import com.example.storereservation.domain.review.dto.ReviewDetail;
import com.example.storereservation.domain.review.dto.ReviewDto;
import com.example.storereservation.domain.review.service.ReviewService;
import com.example.storereservation.domain.store.dto.StoreDetail;
import com.example.storereservation.domain.store.dto.StoreListQuery;
import com.example.storereservation.domain.store.service.StoreService;
import com.example.storereservation.global.type.StoreSortType;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final ReviewService reviewService;

    /**
     * 매장 검색
     * @param page 페이지 번호
     * @param input storeName, sortType( ALL, ALPHABET, RATING, RATING_COUNT, DISTANCE)
     * @return 검색된 매장 리스트
     */
    @ApiOperation(value = "매장 검색", notes = "@RequestBody에 lat, lnt 값을 포함시키고 sortType=DISTANCE로 설정할 경우 가까운 거리 순으로 조회된다.")
    @GetMapping("/list")
    public ResponseEntity<?> storeList(@RequestParam(value = "p", defaultValue = "1") Integer page,
                                       @RequestBody StoreListQuery input) {
        if (input.getSortType().equals(StoreSortType.DISTANCE)) {
            return ResponseEntity.ok(storeService.getStoreListByStoreNameAndDistance(input, page - 1));
        } else {
            return ResponseEntity.ok(storeService.getStoreListByStoreNameAndSortType(input, page - 1));
        }
    }

    /**
     * 매장 상세
     * @param name 매장 명
     * @return 매장 상세 정보
     */
    @ApiOperation("매장 상세 보기")
    @GetMapping("/detail")
    public ResponseEntity<StoreDetail> storeDetail(@RequestParam String name) {
        StoreDetail findStore = storeService.findByStoreName(name);
        return ResponseEntity.ok(findStore);
    }

    /**
     * 매장 별 리뷰 목록 확인
     * @param input storeName, sort[LATEST(최신 순) / RATING_DESC(별점 높은 순) / RATING_ASC(별점 낮은 순)]
     * @param page 페이지 번호 (default=1)
     * @return 매장 별 리뷰 목록
     */
    @ApiOperation("리뷰 목록 확인 - 매장 별")
    @GetMapping("/review")
    public ResponseEntity<Page<ReviewDetail>> reviewListByStoreId(@RequestBody ReviewListInput input,
                                                                  @RequestParam(name = "p", defaultValue = "1") Integer page) {
        Page<ReviewDto> list = reviewService.reviewListByStoreName(input.getStoreName(), input.getSortType(), page - 1);
        Page<ReviewDetail> responseList = list.map(ReviewDetail::fromDto);
        return ResponseEntity.ok(responseList);
    }
}
