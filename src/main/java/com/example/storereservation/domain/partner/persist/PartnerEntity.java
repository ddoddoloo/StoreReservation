package com.example.storereservation.domain.partner.persist;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity(name = "PARTNER")
public class PartnerEntity implements UserDetails {

    @Id
    private String partnerId;
    private String password;
    private String partnerName;
    private String phone;
    private Long storeId;
    private String storeName;
    private String memberType; // ROLE_PARTNER
    private LocalDateTime createAt;
    private LocalDateTime updateDt;

    public void setStore(Long storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(memberType));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.partnerId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
