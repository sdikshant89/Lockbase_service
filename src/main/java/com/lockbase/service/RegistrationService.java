package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.CryptoUtil;
import com.lockbase.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final LoginUserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;

    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        try{
            LoginUser user = populateNewUser(userDTO);

            String otp = emailService.generateOtp();
            user.setOtp(passwordUtil.hashPass(otp));
            user.setOtpExpiry(emailService.getExpiryTimestamp(2));
            LoginUser savedUser = userRepository.save(user);

            boolean sent = emailService.sendOtp(userDTO.getEmail(), otp);
            if (!sent) {
                return createResponse(savedUser, "OTP_FAILED", "User created but OTP could not be sent. Please resend.");
            }

            return createResponse(savedUser);
        }catch (Exception e){
            return UserResponseDTO.builder()
                    .status("FAILED")
                    .message("An unexpected error occurred during registration.")
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    public UserResponseDTO loginUser(UserDTO userDTO){
        Optional<LoginUser> user =  userRepository.findByEmail(userDTO.getEmail());
        return null;
    }

    public boolean resendOtp(String email) {
        Optional<LoginUser> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        LoginUser user = userOpt.get();

        String otp = emailService.generateOtp();
        user.setOtp(passwordUtil.hashPass(otp));
        user.setOtpExpiry(emailService.getExpiryTimestamp(2));

        userRepository.save(user);

        return emailService.sendOtp(email, otp);
    }

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