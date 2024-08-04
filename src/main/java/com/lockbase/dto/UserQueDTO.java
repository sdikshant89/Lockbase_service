package com.lockbase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserQueDTO {

    private String email;
    private Integer userId;
}
