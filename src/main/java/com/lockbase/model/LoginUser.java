package com.lockbase.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "login_user")
public class LoginUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_user_id")
    private Integer id;

    @Column(name = "username"/*, length = 15, unique = true*/)
    private String username;

    @Column(name = "email"/*, unique = true*/)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "country_code")
    private String country_code;

    @Column(name = "ph_number")
    private String phone_number;

    @Column(name = "create_date"/*, updatable = false*/)
    private Timestamp createDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "otp", length = 6)
    private String otp;

    @Column(name = "otp_expiry")
    private Timestamp otpExpiry;

    @Column(name = "iv_pass", nullable = false, columnDefinition = "TEXT")
    private String ivPass;

    @Column(name = "salt_pass", nullable = false, columnDefinition = "TEXT")
    private String saltPass;

    @Column(name = "enc_prk_pass", nullable = false, columnDefinition = "TEXT")
    private String encPrkPass;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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
