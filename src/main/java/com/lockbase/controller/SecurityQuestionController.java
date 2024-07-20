package com.lockbase.controller;

import com.lockbase.model.SecurityQuestion;
import com.lockbase.service.SecurityQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lockbase/SecQue")
public class SecurityQuestionController {

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/get_questions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecurityQuestion> findAllQuestions(){
        return securityQuestionService.findAllQuestions();
    }
}
