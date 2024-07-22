package com.lockbase.service;

import com.lockbase.model.MapUserSeQue;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.repository.MapUserSeQueRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityQuestionService {

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    private MapUserSeQueRepository mapUserSeQueRepository;

    public List<SecurityQuestion> findAllQuestions(){
        List<SecurityQuestion> questions = null;
        try{
            questions = securityQuestionRepository.findAll();
        }catch(Exception e){
            System.out.println(e);
        }
        return questions;
    }

    // Test method delete after testing
    public List<MapUserSeQue> getAll(){
        List<MapUserSeQue> queMap = null;
        try{
            queMap = mapUserSeQueRepository.findAll();
        }catch(Exception e){
            System.out.println(e);
        }
        return queMap;
    }

    public List<MapUserSeQue> findUserSpecific(){
        // This method would get users email and user ID from request, and it will send the
        // questions and answers of that particular user in order to complete 2FA. (This step
        // could be done before sending email for 2FA)
        return null;
    }

    public Boolean saveInfo(){
        //This method would save the question selected by user and its answers in the DB. Have to
        // first create a type of DTO which would take list of question(and maybe its ID too) and
        // answers to those questions and save them in DB.
        return null;
    }
}
