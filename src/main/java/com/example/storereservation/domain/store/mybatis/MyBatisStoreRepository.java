package com.example.storereservation.domain.store.mybatis;

import com.example.storereservation.domain.store.dto.StoreDto;
import com.example.storereservation.domain.store.dto.StoreListQuery;
import com.example.storereservation.global.type.PageConst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisStoreRepository {

    private final StoreMapper storeMapper;

    /**
     * 매장 이름으로 거리 순 정렬된 매장 리스트 조회
     * @param input 매장 검색 조건
     * @param page 페이지 번호
     * @return 거리 순으로 정렬된 매장 리스트
     */
    public List<StoreDto> findByStoreNameOrderByDistance(StoreListQuery input, Integer page) {
        Integer size = PageConst.STORE_LIST_PAGE_SIZE;
        return storeMapper.findStoreListOrderByDistance(
                input.getStoreName(),
                input.getLat(),
                input.getLnt(),
                size * page, size);
    }
}
