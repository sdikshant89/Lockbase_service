package com.lockbase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private String username;
    private String email;
    private String password;
    private String country_code;
    private String phone_number;
}
