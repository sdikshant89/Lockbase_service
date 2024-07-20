package com.lockbase.service;

import com.lockbase.model.SecurityQuestion;
import com.lockbase.repository.SecurityQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityQuestionService {

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    public List<SecurityQuestion> findAllQuestions(){
        List<SecurityQuestion> questions = null;
        try{
            questions = securityQuestionRepository.findAll();
        }catch(Exception e){
            System.out.println(e);
        }
        return questions;
    }
}
