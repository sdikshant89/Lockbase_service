package com.lockbase.util;

import com.lockbase.dto.SecurityAnswerDTO;
import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.model.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import java.util.Comparator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserUtil {

    private final PasswordUtil passwordUtil;

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
            newUser.setEmail(userDTO.getEmail().trim().toLowerCase());
            newUser.setCountry_code(userDTO.getCountry_code());
            newUser.setPhone_number(userDTO.getPhone_number().replaceAll("[^0-9+]", ""));
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
}
