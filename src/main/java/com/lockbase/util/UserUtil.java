package com.lockbase.util;

import com.lockbase.dto.SecurityAnswerDTO;
import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.model.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import java.util.Comparator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserUtil {

    private final PasswordUtil passwordUtil;

    @Value("${security.refresh-token.cookie-name:refreshToken}")
    private String refreshCookieName;

    @Value("${security.refresh-token.ttl-days:30}")
    private long refreshTtlDays;

    @Value("${security.refresh-token.cookie-secure:true}")
    private boolean cookieSecure;

    @Value("${security.refresh-token.cookie-samesite:Strict}")
    private String sameSite; // Strict / Lax / None

    @Value("${security.refresh-token.cookie-path:/api/auth/refresh}")
    private String cookiePath;

    public LoginUser populateNewUser(UserDTO userDTO) {
        try {

            byte[] prkBytes = CryptoUtil.generateRandomBytes(32);
            String prkPlaintext = CryptoUtil.toBase64(prkBytes);

            String combinedSecret = userDTO.getSecurityQueAns().stream()
                    .sorted(Comparator.comparing(SecurityAnswerDTO::getQuestionId))
                    .map(a -> a.getAnswer().trim().toLowerCase())
                    .collect(Collectors.joining("||"));

            byte[] saltPass = CryptoUtil.generateSalt();
            byte[] ivPass = CryptoUtil.generateIv();

            byte[] saltRecovery = CryptoUtil.generateSalt();
            byte[] ivRecovery = CryptoUtil.generateIv();

            String encryptedPrkPass = CryptoUtil.encrypt(prkPlaintext, userDTO.getPassword(), saltPass,
                    ivPass);
            String encryptedPrkRecovery = CryptoUtil.encrypt(prkPlaintext, combinedSecret, saltRecovery,
                    ivRecovery);

            LoginUser newUser = new LoginUser();
            newUser.setUsername(userDTO.getUsername().trim().replaceAll("\\s+", " "));
            newUser.setEmail(userDTO.getEmail().trim());
            newUser.setCountry_code(userDTO.getCountryCode());
            newUser.setPhone_number(userDTO.getCellNumber().replaceAll("[^0-9+]", ""));
            newUser.setCreateDate(new Timestamp(new Date().getTime()));
            newUser.setPassword(passwordUtil.hashPass(userDTO.getPassword()));

            newUser.setIvPass(CryptoUtil.toBase64(ivPass));
            newUser.setSaltPass(CryptoUtil.toBase64(saltPass));
            newUser.setEncPrkPass(encryptedPrkPass);

            newUser.setIvRecovery(CryptoUtil.toBase64(ivRecovery));
            newUser.setSaltRecovery(CryptoUtil.toBase64(saltRecovery));
            newUser.setEncPrkRecovery(encryptedPrkRecovery);
            return newUser;

        } catch (Exception e) {
            throw new InternalServerException("User registration failed during encryption step.", e);
        }
    }

    public UserResponseDTO createResponse(LoginUser user) {
        return createResponse(user, "OTP_PENDING", "User registered successfully. Please verify " +
                "OTP.", Boolean.FALSE);
    }

    public UserResponseDTO createResponse(LoginUser user, String status, String message,
                                          Boolean success) {
        return UserResponseDTO.builder()
                .email(user.getEmail())
                .status(status)
                .message(message)
                .success(success)
                .otpExpiry(user.getOtpExpiry())
                .build();
    }
    public ResponseCookie buildRefreshCookie(String rawRefreshToken) {
        // Max-Age in seconds
        long maxAgeSeconds = refreshTtlDays * 24L * 60L * 60L;

        return ResponseCookie.from(refreshCookieName, rawRefreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(maxAgeSeconds)
                .sameSite(sameSite)
                .build();
    }

    public ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path(cookiePath)
                .maxAge(0)
                .sameSite(sameSite)
                .build();
    }
}
