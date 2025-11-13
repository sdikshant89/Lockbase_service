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

    private Integer id;
    private String email;
    private Timestamp createDate;
    private String errorMessage;
    private String status;
    private String message;
    private String code;
    private Timestamp otpExpiry;
}
