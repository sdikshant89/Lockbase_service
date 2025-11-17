package com.lockbase.service;

import com.lockbase.dto.SecurityAnswerDTO;
import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.OtpSendFailedException;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.model.MapUserSeQue;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.repository.MapUserSeQueRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import com.lockbase.util.PasswordUtil;
import com.lockbase.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserUtil userUtil;
    private final JWTService jwtService;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;

    private final LoginUserRepository userRepository;
    private final MapUserSeQueRepository mapUserSeQueRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    @Transactional
    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        validateSecurityQuestions(userDTO.getSecurityQueAns());

        try{
            LoginUser user = userUtil.populateNewUser(userDTO);
            LoginUser savedUser = userRepository.save(user);
            saveSecurityQuestionAnswers(savedUser, userDTO.getSecurityQueAns());

            sendOtp(savedUser);

            return userUtil.createResponse(savedUser, "OTP_SENT", "User registered successfully. OTP sent.",
                    Boolean.TRUE);
        }catch (Exception e){
            throw new InternalServerException("User registration failed during encryption step.", e);
        }
    }

    public UserResponseDTO sendOtp(LoginUser user) {
        String otp = emailService.generateOtp();
        user.setOtp(passwordUtil.hashPass(otp));
        user.setOtpExpiry(emailService.getExpiryTimestamp(10));

        userRepository.save(user);
        boolean sent = emailService.sendOtp(user.getEmail(), otp);
        if (!sent) {
            throw new OtpSendFailedException("User created but OTP could not be sent. Please resend");
        }
        return UserResponseDTO.builder()
                .email(user.getEmail())
                .status("OTP_SENT")
                .message("OTP send successfully")
                .success(Boolean.TRUE)
                .build();
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
            Boolean isMatch = passwordUtil.checkPass(user.getOtp(), otp);
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

    public UserResponseDTO loginUser(UserDTO userDTO){
        //Optional<LoginUser> user =  userRepository.findByEmail(userDTO.getEmail());
        return null;
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

    private void saveSecurityQuestionAnswers(LoginUser user, List<SecurityAnswerDTO> answers){

        List<MapUserSeQue> list = answers.stream().map(dto -> {
            String normalizedAnswer = dto.getAnswer().trim().toLowerCase();
            SecurityQuestion question = securityQuestionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid security question ID: " + dto.getQuestionId())
                    );
            MapUserSeQue entry = new MapUserSeQue();
            entry.setUser(user);
            entry.setSecurityQuestion(question);
            entry.setAnswer(passwordUtil.hashPass(normalizedAnswer));
            return entry;
        }).toList();

        mapUserSeQueRepository.saveAll(list);
    }

    private void validateSecurityQuestions(List<SecurityAnswerDTO> answers) {

        Set<Integer> ids = new HashSet<>();
        if (answers == null || answers.size() != 2) {
            throw new IllegalArgumentException("Exactly 2 security questions are required.");
        }
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