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
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;

    // "register_user"
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO user){
        UserResponseDTO response = authService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // "verify_otp"
    @PostMapping("/verify_otp")
    public ResponseEntity<UserResponseDTO> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        UserResponseDTO response = authService.verifyOtp(request.getEmail(), request.getOtp());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Resend OTP
    // Forgot Password

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        // Get the cookie named "refreshToken"
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        String refreshToken = (cookie != null) ? cookie.getValue() : null;

        // Delegate logic to the service layer
        return authService.refreshAccessToken(refreshToken);
    }
}
