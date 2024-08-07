package com.lockbase.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapSecQueDTO {

    private Integer userId;
    private List<SecurityQuestionDTO> queAns;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecurityQuestionDTO {
        private Integer questionId;
        private String answer;
    }
}
