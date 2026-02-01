package com.lockbase.service;

import com.lockbase.dto.*;
import com.lockbase.exception.*;
import com.lockbase.model.LoginUser;
import com.lockbase.model.MapUserSeQue;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.repository.MapUserSeQueRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import com.lockbase.util.PasswordUtil;
import com.lockbase.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserUtil userUtil;
    private final JWTService jwtService;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;

    private final LoginUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final MapUserSeQueRepository mapUserSeQueRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    public record LoginResult(LoginResponseDTO body, String rawRefreshToken) {}
    public record RefreshResult(RefreshResponseDTO body, String rawRefreshToken) {}

    @Transactional
    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        validateSecurityQuestions(userDTO.getSecurityQueAns());
        LoginUser user = userUtil.populateNewUser(userDTO);
        LoginUser savedUser = userRepository.save(user);
        saveSecurityQuestionAnswers(savedUser, userDTO.getSecurityQueAns());

        sendOtp(savedUser);

        return userUtil.createResponse(savedUser, "OTP_SENT", "User registered successfully. OTP sent.",
                Boolean.TRUE);
    }

    public void sendOtp(LoginUser user) {
        String otp = emailService.generateOtp();
        user.setOtp(passwordUtil.hashPass(otp));
        user.setOtpExpiry(emailService.getExpiryTimestamp(10));

        userRepository.save(user);
        boolean sent = emailService.sendOtp(user.getEmail(), otp);
        if (!sent) {
            throw new OtpDeliveryFailedException("OTP could not be sent. Please resend");
        }
    }

    public UserResponseDTO resendOtp(String email){
        LoginUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // TODO: Add logic to prevent spamming
        sendOtp(user);
        return UserResponseDTO.builder()
                .email(email)
                .success(Boolean.TRUE)
                .message("Success! OTP sent")
                .build();
    }

    public UserResponseDTO verifyOtp(String email, String otp) {
            LoginUser user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("No user found, check email and try again!"));

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (user.getOtpExpiry() == null || user.getOtpExpiry().before(now)) {
                throw new GenericOtpException("OTP expired, generate new OTP and try again!");
            }

            if (!passwordUtil.checkPass(user.getOtp(), otp)) {
                throw new GenericOtpException("OTP mismatch, check OTP and try again!");
            }
            user.setOtp(null);
            user.setOtpExpiry(null);
            user.setVerified(true);
            userRepository.save(user);

            return UserResponseDTO.builder()
                    .email(email)
                    .success(Boolean.TRUE)
                    .message("Success! User verified")
                    .build();
    }

    @Transactional
    public LoginResult login(LoginRequestDTO dto, HttpServletRequest request) {
        LoginUser user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!Boolean.TRUE.equals(passwordUtil.checkPass(user.getPassword(), dto.getPassword()))) {
            throw new AuthException("Invalid credentials");
        }

        String accessToken = jwtService.issueAccessToken(user);

        RefreshTokenService.IssuedRefreshToken issued = refreshTokenService.create(user, request);

        LoginResponseDTO body = LoginResponseDTO.builder()
                .accessToken(accessToken)
                .encPrkPass(user.getEncPrkPass())
                .saltPass(user.getSaltPass())
                .ivPass(user.getIvPass())
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .build();

        return new LoginResult(body, issued.rawToken());
    }

    @Transactional
    public RefreshResult refresh(String rawRefreshTokenFromCookie, HttpServletRequest request) {
        RefreshTokenService.IssuedRefreshToken rotated =
                refreshTokenService.rotate(rawRefreshTokenFromCookie, request);

        LoginUser user = rotated.entity().getUser();
        String accessToken = jwtService.issueAccessToken(user);

        RefreshResponseDTO body = RefreshResponseDTO.builder()
                .accessToken(accessToken)
                .build();

        return new RefreshResult(body, rotated.rawToken());
    }

    @Transactional
    public void logout(String rawRefreshTokenFromCookie) {
        refreshTokenService.revoke(rawRefreshTokenFromCookie);
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