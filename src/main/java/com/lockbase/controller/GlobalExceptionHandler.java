package com.lockbase.controller;

import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@SuppressWarnings("unused")
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createNullableObject(e,
                "USER_ALREADY_EXISTS"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserResponseDTO> handleUserNotFoundFail(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createNullableObject(e,
                "USER_NOT_FOUND"));
    }

    @ExceptionHandler(OtpDeliveryFailedException.class)
    public ResponseEntity<UserResponseDTO> handleOtpFail(OtpDeliveryFailedException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(createNullableObject(e,
                "OTP_FAILED"));
    }

    @ExceptionHandler(GenericOtpException.class)
    public ResponseEntity<UserResponseDTO> handleOtpCheckFail(GenericOtpException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createNullableObject(e,
                "INCORRECT_OTP"));
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

    public UserResponseDTO createNullableObject(Exception e, String status){
        return UserResponseDTO.builder()
                .status(status)
                .errorMessage(e.getMessage())
                .success(Boolean.FALSE)
                .build();
    }
}
