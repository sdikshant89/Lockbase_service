package com.lockbase.controller;

import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.OtpSendFailedException;
import com.lockbase.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createNullableObject(e,
                "USER_ALREADY_EXISTS"));
    }

    @ExceptionHandler(OtpSendFailedException.class)
    public ResponseEntity<UserResponseDTO> handleOtpFail(OtpSendFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(createNullableObject(e,
                "OTP_FAILED"));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<UserResponseDTO> handleInternalServerError(InternalServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNullableObject(e,
                "INTERNAL_ERROR"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserResponseDTO> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNullableObject(e,
                "INTERNAL_SERVER_ERROR"));
    }

    public UserResponseDTO createNullableObject(Exception e, String code){
        return UserResponseDTO.builder()
                .email(null)
                .id(null)
                .createDate(null)
                .errorMessage(e.getMessage())
                .code(code)
                .build();
    }
}
