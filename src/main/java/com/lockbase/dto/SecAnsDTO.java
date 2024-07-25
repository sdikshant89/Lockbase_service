package com.lockbase.dto;

import com.lockbase.model.SecurityQuestion;

public class SecAnsDTO {

    public SecAnsDTO(Integer id, String question, String answer) {
        this.id = id;
        Question = question;
        this.answer = answer;
    }

    private Integer id;
    private String Question;
    private String answer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
