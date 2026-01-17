package com.lockbase.service;

import com.lockbase.exception.InvalidRefreshTokenException;
import com.lockbase.model.LoginUser;
import com.lockbase.model.RefreshToken;
import com.lockbase.repository.RefreshTokenRepository;
import com.lockbase.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // @Value is used to get key from application properties
    // @Value("${property.key:defaultValue}") syntax
    @Value("${security.refresh-token.ttl-days:30}")
    private long refreshTtlDays;

    // We use this along with refresh token
    // So even if hackers get hold of DB they can't decipher how are we hashing
    // This isn't same as salt
    @Value("${security.refresh-token.hash-pepper:}")
    private String refreshHashPepper;

    @Value("${security.refresh-token.bytes:32}")
    private int refreshTokenBytes;

    // immutable “data carrier” class creation - record
    // record holds private final fields
    public record IssuedRefreshToken(String rawToken, RefreshToken entity) {}

    private String generateOpaqueToken(int length) {
        byte[] buf = CryptoUtil.generateRandomBytes(length);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    @Transactional
    public IssuedRefreshToken create(LoginUser user, HttpServletRequest request) {
        String rawToken = generateOpaqueToken(refreshTokenBytes);
        String tokenHash = hashRefreshToken(rawToken);

        Instant now = Instant.now();
        Instant expiresAt = now.plus(Duration.ofDays(refreshTtlDays));

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .createdAt(now)
                .expiresAt(expiresAt)
                .revokedAt(null)
                .replacedByHash(null)
                .userAgent(extractUserAgent(request))
                .ipAddress(extractClientIp(request))
                .lastUsedAt(now)
                .build();

        RefreshToken saved = refreshTokenRepository.save(entity);
        return new IssuedRefreshToken(rawToken, saved);
    }

    @Transactional
    public RefreshToken validateAndUpdate(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Missing refresh token");
        }

        String tokenHash = hashRefreshToken(rawRefreshToken);
        RefreshToken rt = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if (rt.getRevokedAt() != null) {
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        if (rt.getExpiresAt() != null && rt.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        rt.setLastUsedAt(Instant.now());
        return rt;
    }

    @Transactional
    public IssuedRefreshToken rotate(String oldRawRefreshToken, HttpServletRequest request) {
        RefreshToken old = validateAndUpdate(oldRawRefreshToken);

        IssuedRefreshToken fresh = create(old.getUser(), request);

        old.setRevokedAt(Instant.now());
        old.setReplacedByHash(hashRefreshToken(fresh.rawToken()));

        refreshTokenRepository.save(old);

        return fresh;
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) return;

        String tokenHash = hashRefreshToken(rawRefreshToken);
        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash(tokenHash);

        found.ifPresent(rt -> {
            if (rt.getRevokedAt() == null) {
                rt.setRevokedAt(Instant.now());
                refreshTokenRepository.save(rt);
            }
        });
    }

    @Transactional
    public int revokeAllForUser(Long userId) {
        return refreshTokenRepository.revokeAllActiveByUserId(userId, Instant.now());
    }

    @Transactional
    public int cleanupExpired() {
        return refreshTokenRepository.deleteAllExpiredBefore(Instant.now());
    }

    private String hashRefreshToken(String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String material = (refreshHashPepper == null || refreshHashPepper.isBlank())
                    ? rawToken
                    : (refreshHashPepper + ":" + rawToken);

            byte[] digest = md.digest(material.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash refresh token", e);
        }
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) return null;
        return request.getHeader("User-Agent");
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return null;

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
