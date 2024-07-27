package com.lockbase.dto;

import java.util.List;

public class MapSecQueDTO {

    public MapSecQueDTO(Integer userId, List<SecurityQuestionDTO> queAns) {
        this.userId = userId;
        this.queAns = queAns;
    }

    private Integer userId;
    private List<SecurityQuestionDTO> queAns;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<SecurityQuestionDTO> getQueAns() {
        return queAns;
    }

    public void setQueAns(List<SecurityQuestionDTO> queAns) {
        this.queAns = queAns;
    }

    public class SecurityQuestionDTO {

        public SecurityQuestionDTO(Integer questionId, String answer) {
            this.questionId = questionId;
            this.answer = answer;
        }

        private Integer questionId;
        private String answer;

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
