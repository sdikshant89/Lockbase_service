package com.lockbase.dto;

public class SecAnsDTO {

    public SecAnsDTO(Integer queId, String question, String answer) {
        this.queId = queId;
        this.question = question;
        this.answer = answer;
    }

    private Integer queId;
    private String question;
    private String answer;

    public Integer getQueId() {
        return queId;
    }

    public void setQueId(Integer queId) {
        this.queId = queId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
