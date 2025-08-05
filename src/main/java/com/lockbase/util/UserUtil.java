package com.lockbase.util;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.model.LoginUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
@AllArgsConstructor
public class UserUtil {

    private final PasswordUtil passwordUtil;

    public LoginUser populateNewUser(UserDTO userDTO) {
        try {

            byte[] prkBytes = CryptoUtil.generateRandomBytes(32);
            String prkPlaintext = CryptoUtil.toBase64(prkBytes);

            byte[] salt = CryptoUtil.generateSalt();
            byte[] iv = CryptoUtil.generateIv();

            String encryptedPrk = CryptoUtil.encrypt(prkPlaintext, userDTO.getPassword(), salt, iv);
            String encodedPassword = passwordUtil.hashPass(userDTO.getPassword());

            LoginUser newUser = new LoginUser();
            BeanUtils.copyProperties(userDTO, newUser);
            newUser.setCreateDate(new Timestamp(new Date().getTime()));
            newUser.setPassword(encodedPassword);
            newUser.setIvPass(CryptoUtil.toBase64(iv));
            newUser.setSaltPass(CryptoUtil.toBase64(salt));
            newUser.setEncPrkPass(encryptedPrk);
            return newUser;

        } catch (Exception e) {
            throw new InternalServerException("User registration failed during encryption step.", e);
        }
    }

    public UserResponseDTO createResponse(LoginUser user) {
        return createResponse(user, "OTP_PENDING", "User registered successfully. Please verify OTP.");
    }

    public UserResponseDTO createResponse(LoginUser user, String status, String message) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createDate(user.getCreateDate())
                .status(status)
                .message(message)
                .otpExpiry(user.getOtpExpiry())
                .build();
    }
}
