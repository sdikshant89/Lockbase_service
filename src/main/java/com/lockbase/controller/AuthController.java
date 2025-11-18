package com.lockbase.controller;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.VerifyOtpRequestDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lockbase/auth")
public class AuthController {

    // No need of autowired if use @RequiredArgsConstructor on the class itself
    private final AuthService authService;

    // "register_user"
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO user){
        UserResponseDTO response = authService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // "send_otp"
//    @PostMapping(value = "/send_otp", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UserResponseDTO> resendOtp(@RequestParam("email") String email) {
//        UserResponseDTO response = authService.sendOtp(email);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    // "login_user"
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/login_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginUser(@RequestBody UserDTO user){
        Object response = authService.loginUser(user);
        if (response == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // "verify_otp"
    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        try {
            boolean verified = authService.verifyOtp(request.getEmail(), request.getOtp());
            if (verified) {
                return ResponseEntity.ok("OTP verified successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification failed.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // Get the cookie named "refreshToken"
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        String refreshToken = (cookie != null) ? cookie.getValue() : null;

        // Delegate logic to the service layer
        return authService.refreshAccessToken(refreshToken);
    }
}
