package com.example.storereservation.domain.partner.controller;

import com.example.storereservation.domain.partner.dto.PartnerDto;
import com.example.storereservation.domain.partner.dto.RegisterPartner;
import com.example.storereservation.domain.partner.persist.PartnerEntity;
import com.example.storereservation.domain.partner.service.PartnerService;
import com.example.storereservation.domain.store.dto.AddStore;
import com.example.storereservation.domain.store.dto.EditStore;
import com.example.storereservation.domain.store.dto.StoreDto;
import com.example.storereservation.global.exception.ErrorCode;
import com.example.storereservation.global.exception.MyException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/partner")
public class PartnerController {

    private final PartnerService partnerService;

    @ApiOperation(value = "파트너 회원가입")
    @PostMapping("/register")
    public ResponseEntity<RegisterPartner.Response> registerPartner(@RequestBody RegisterPartner.Request request) {
        PartnerDto registeredManager = partnerService.register(request);
        return ResponseEntity.ok(RegisterPartner.Response.fromDto(registeredManager));
    }

    @ApiOperation(value = "매장 등록", notes = "파트너 ID 하나 당 하나의 매장만 등록할 수 있다.")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @PostMapping("/register-store/{partnerId}")
    public ResponseEntity<AddStore.Response> registerStore(
            @PathVariable String partnerId,
            @RequestBody AddStore.Request request,
            @AuthenticationPrincipal PartnerEntity partner) {
        validatePartnerId(partnerId, partner);
        StoreDto savedStore = partnerService.addStore(partnerId, request);
        return ResponseEntity.ok(AddStore.Response.fromDto(savedStore));
    }

    @ApiOperation(value = "매장 정보 수정")
    @PreAuthorize("hasRole('ROLE_PARTNER')")
    @PutMapping("/edit-store/{partnerId}")
    public ResponseEntity<EditStore.Response> editStore(
            @PathVariable String partnerId,
            @RequestBody EditStore.Request request,
            @AuthenticationPrincipal PartnerEntity partner) {
        validatePartnerId(partnerId, partner);
        StoreDto storeDto = partnerService.editStore(partnerId, request);
        return ResponseEntity.ok(EditStore.Response.fromDto(storeDto));
    }

    private void validatePartnerId(String partnerId, PartnerEntity partner) {
        if (!partnerId.equals(partner.getPartnerId())) {
            throw new MyException(ErrorCode.STORE_PARTNER_NOT_MATCH);
        }
    }
}
