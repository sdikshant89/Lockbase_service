package com.lockbase.controller;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.VerifyOtpRequestDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/register")
public class RegistrationController {

    // No need of autowired if use @RequiredArgsConstructor on the class itself
    @Autowired
    private RegistrationService registrationService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO user){
        UserResponseDTO response = registrationService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/login_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginUser(@RequestBody UserDTO user){
        Object response = registrationService.loginUser(user);
        if (response == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/resend_otp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> resendOtp(@RequestParam("email") String email) {
        boolean success = registrationService.resendOtp(email);
        if (success) {
            return new ResponseEntity<>("OTP resent successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to resend OTP. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify_otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        try {
            boolean verified = registrationService.verifyOtp(request.getEmail(), request.getOtp());
            if (verified) {
                return ResponseEntity.ok("OTP verified successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Verification failed.");
        }
    }
}
