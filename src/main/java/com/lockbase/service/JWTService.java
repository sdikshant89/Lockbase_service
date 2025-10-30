package com.lockbase.service;

import com.lockbase.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    // Generates Bytes for Secret Key
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode("35537e78324c4e6b566c28252c36525b76585e327e3f7a2e7a4e25796d");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Map<String, Object> claims, LoginUser user) {
        return generateToken(claims, user, Duration.ofMinutes(45));
    }

    public String generateRefreshToken(LoginUser user) {
        return generateToken(Map.of(), user, Duration.ofDays(7)); // 7 days
    }

    // Checks if the token is valid and returns claims if it is
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
        return claimsResolver.apply(claims);
    }

    // Checks if the claim's subject have username and its not expired.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject);

        // It is stupid to check the username again with the userDetails because we initially got
        // the userDetails entity using the same username from the token -- so ofc it would be
        // the same username -- check class JWTAuthFilter -- function doFilterInternal() where
        // this function is called.
        return (username.equals(userDetails.getUsername())) && !(extractClaim(token, Claims::getExpiration).before(new Date()));
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private String generateToken(Map<String, Object> claims, LoginUser user, Duration duration) {
        Date expiry = new Date(new Date().getTime() + duration.toMillis());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(expiry)
                .signWith(getSignInKey())
                .compact();
    }
}
