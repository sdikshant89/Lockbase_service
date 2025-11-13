package com.lockbase.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityAnswerDTO {
    private Integer questionId;
    private String answer;
}
