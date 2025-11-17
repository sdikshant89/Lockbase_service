package com.lockbase.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDTO {

    private String username;
    private String email;
    private String password;
    private String countryCode;
    private String cellNumber;

    private List<SecurityAnswerDTO> securityQueAns;
}
