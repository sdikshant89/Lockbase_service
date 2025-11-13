package com.lockbase.service;

import com.lockbase.dto.SecurityAnswerDTO;
import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.OtpSendFailedException;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import com.lockbase.util.PasswordUtil;
import com.lockbase.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginUserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;
    private final UserUtil userUtil;
    private final JWTService jwtService;
    private final SecurityQuestionRepository securityQuestionRepository;

    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        try{
            validateSecurityQuestions(userDTO.getSecurityQueAns());
            LoginUser user = userUtil.populateNewUser(userDTO);

            String otp = emailService.generateOtp();
            user.setOtp(passwordUtil.hashPass(otp));
            user.setOtpExpiry(emailService.getExpiryTimestamp(2));
            LoginUser savedUser = userRepository.save(user);

            boolean sent = emailService.sendOtp(userDTO.getEmail(), otp);
            if (!sent) {
                throw new OtpSendFailedException("User created but OTP could not be sent. Please resend");
            }

            // TODO: Save map user security question
            return userUtil.createResponse(savedUser);
        }catch (Exception e){
            throw new InternalServerException("User registration failed during encryption step.", e);
        }
    }

    public UserResponseDTO loginUser(UserDTO userDTO){
        //Optional<LoginUser> user =  userRepository.findByEmail(userDTO.getEmail());
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

    public Boolean verifyOtp(String email, String otp) {
        try{
            Optional<LoginUser> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()){
                return false;
            }
            LoginUser user = userOpt.get();
            if (user.getOtpExpiry() == null || user.getOtpExpiry().before(new Timestamp(System.currentTimeMillis()))) {
                return false;
            }
            Boolean isMatch = passwordUtil.checkPass(user.getOtp(), passwordUtil.hashPass(otp));
            if (!isMatch){
                return false;
            }
            user.setOtp(null);
            user.setOtpExpiry(null);
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }catch (Exception e){
            //Log error
            return false;
        }
    }

    public ResponseEntity<?> refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing refresh token"));
        }

        try {
            String username = jwtService.extractClaim(refreshToken, Claims::getSubject);
            LoginUser user = null;
                    //(LoginUser) userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired"));
            }

            String newAccessToken = jwtService.generateAccessToken(Map.of(), user);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("error", "Invalid refresh token"));
        }
    }

    private void validateSecurityQuestions(List<SecurityAnswerDTO> answers) {

        if (answers == null || answers.size() != 2) {
            throw new IllegalArgumentException("Exactly 2 security questions are required.");
        }

        Set<Integer> ids = new HashSet<>();
        for (SecurityAnswerDTO dto : answers) {
            if (!ids.add(dto.getQuestionId())) {
                throw new IllegalArgumentException("Duplicate security question selected: " + dto.getQuestionId());
            }

            SecurityQuestion q =
                    securityQuestionRepository.findById(dto.getQuestionId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + dto.getQuestionId()));

            if (dto.getAnswer() == null || dto.getAnswer().trim().isEmpty()) {
                throw new IllegalArgumentException("Answer cannot be empty for question: " + q.getQuestion());
            }
        }
    }
}