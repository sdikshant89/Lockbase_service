package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.PasswordUtil;
import com.lockbase.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final LoginUserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final EmailService emailService;
    private final UserUtil userUtil;

    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        try{
            LoginUser user = userUtil.populateNewUser(userDTO);

            String otp = emailService.generateOtp();
            user.setOtp(passwordUtil.hashPass(otp));
            user.setOtpExpiry(emailService.getExpiryTimestamp(2));
            LoginUser savedUser = userRepository.save(user);

            boolean sent = emailService.sendOtp(userDTO.getEmail(), otp);
            if (!sent) {
                return userUtil.createResponse(savedUser, "OTP_FAILED", "User created but OTP " +
                        "could not be sent. Please resend.");
            }

            return userUtil.createResponse(savedUser);
        }catch (Exception e){
            return UserResponseDTO.builder()
                    .status("FAILED")
                    .message("An unexpected error occurred during registration.")
                    .errorMessage(e.getMessage())
                    .build();
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
}