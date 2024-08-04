package com.lockbase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class MapSecQueDTO {

    private Integer userId;
    private List<SecurityQuestionDTO> queAns;

    @Data
    @AllArgsConstructor
    public class SecurityQuestionDTO {

        private Integer questionId;
        private String answer;
    }
}
