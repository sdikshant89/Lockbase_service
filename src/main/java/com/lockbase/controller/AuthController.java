package com.lockbase.controller;

import com.lockbase.dto.*;
import com.lockbase.service.AuthService;
import com.lockbase.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lockbase/auth")
@SuppressWarnings("unused")
public class AuthController {

    private final AuthService authService;
    private final UserUtil userUtil;

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
    @PatchMapping("/resend_otp")
    public ResponseEntity<UserResponseDTO> resendOtp(@RequestBody VerifyOtpRequestDTO request) {
        UserResponseDTO response = authService.resendOtp(request.getEmail());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // "login_user"
    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/login_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO dto, HttpServletRequest request){
        AuthService.LoginResult result = authService.login(dto, request);

        ResponseCookie cookie = userUtil.buildRefreshCookie(result.rawRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.body());
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponseDTO> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request
    ) {
        AuthService.RefreshResult result = authService.refresh(refreshToken, request);

        ResponseCookie cookie = userUtil.buildRefreshCookie(result.rawRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        ResponseCookie cleared = userUtil.clearRefreshCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cleared.toString())
                .build();
    }

    // Forgot Password
}
