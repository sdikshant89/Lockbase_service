package com.lockbase.repository;

import com.lockbase.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUser_Id(Long userId);

    @Modifying
    @Query("""
        UPDATE RefreshToken rt
        SET rt.revokedAt = :revokedAt
        WHERE rt.user.id = :userId AND rt.revokedAt IS NULL
    """)
    int revokeAllActiveByUserId(Long userId, Instant revokedAt);

    @Modifying
    @Query("""
        DELETE FROM RefreshToken rt
        WHERE rt.expiresAt < :cutoff
    """)
    int deleteAllExpiredBefore(Instant cutoff);
}
