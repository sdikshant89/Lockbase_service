package com.lockbase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private String email;
    private String status;
    private String message;
    private String errorMessage;
    private Boolean success;
    private Timestamp otpExpiry;

}
