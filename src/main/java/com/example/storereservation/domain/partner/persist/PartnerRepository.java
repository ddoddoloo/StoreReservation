package com.example.storereservation.domain.partner.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, String> {

    /**
     * partnerId로 파트너가 존재하는지 확인합니다.
     *
     * @param partnerId 파트너 ID
     * @return 주어진 ID를 가진 파트너가 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByPartnerId(String partnerId);

    /**
     * partnerId로 파트너를 찾습니다.
     *
     * @param partnerId 파트너 ID
     * @return 파트너 엔터티가 존재하면 해당 엔터티를 포함한 Optional, 존재하지 않으면 빈 Optional
     */
    Optional<PartnerEntity> findByPartnerId(String partnerId);
}
