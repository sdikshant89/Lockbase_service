package com.lockbase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SecAnsDTO {

    private Integer queId;
    private String question;
    private String answer;
}
