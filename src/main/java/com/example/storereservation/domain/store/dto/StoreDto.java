package com.example.storereservation.domain.store.dto;

import com.example.storereservation.domain.store.persist.StoreEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreDto {

    private Long id;
    private String partnerId;
    private String storeName;
    private String storeAddr;
    private String text;
    private double lat;
    private double lnt;
    private double distance;
    private double rating;
    private Long ratingCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static StoreDto fromEntity(StoreEntity store) {
        return StoreDto.builder()
                .id(store.getId())
                .partnerId(store.getPartnerId())
                .storeName(store.getStoreName())
                .storeAddr(store.getStoreAddr())
                .text(store.getText())
                .lat(store.getLat())
                .lnt(store.getLnt())
                .rating(store.getRating())
                .ratingCount(store.getRatingCount())
                .createAt(store.getCreateAt())
                .updateAt(store.getUpdateAt())
                .build();
    }
}
