package com.lockbase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    // To be set as cookie
    private String accessToken;

    // PRK package
    private String encPrkPass;
    private String saltPass;
    private String ivPass;

    // UserInfo
    private Integer userId;
    private String email;
    private String username;
}
