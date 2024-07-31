package com.lockbase.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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

    // Generates token using claims and secret key
    public String generateToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 24 * 3)))
                .signWith(getSignInKey())
                .compact();
    }

    // Extract claim from the request token
    private Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject);
        return (username.equals(userDetails.getUsername())) && !(extractClaim(token, Claims::getExpiration).before(new Date()));
    }
}
