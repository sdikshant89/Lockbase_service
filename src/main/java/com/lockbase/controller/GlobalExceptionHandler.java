package com.lockbase.controller;

import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<UserResponseDTO> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(createNullableObject(e));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<UserResponseDTO> handleInternalServerError(InternalServerException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNullableObject(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserResponseDTO> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createNullableObject(e));
    }

    public UserResponseDTO createNullableObject(Exception e){
        return UserResponseDTO.builder()
                .username(null)
                .email(null)
                .id(null)
                .createDate(null)
                .errorMessage(e.getMessage())
                .build();
    }
}
