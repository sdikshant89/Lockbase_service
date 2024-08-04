package com.lockbase.service;

import com.lockbase.dto.SecAnsDTO;
import com.lockbase.dto.UserQueDTO;
import com.lockbase.model.MapUserSeQue;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.MapUserSeQueRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import com.lockbase.repository.LoginUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SecurityQuestionService {

    @Autowired
    private LoginUserRepository userRepository;

    @Autowired
    private MapUserSeQueRepository mapUserSeQueRepository;

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

    //(This step could be done before sending email for 2FA)
    //Change output of the code
    public List<SecAnsDTO> getUserSecQue(UserQueDTO userQueDTO){
        LoginUser activeUser = null;
        List<SecAnsDTO> userSecQue = null;
        try{
            activeUser = userRepository.findUserByEmailAndId(userQueDTO.getEmail(),
                    userQueDTO.getUserId());
            if(Objects.nonNull(activeUser)){
                userSecQue = mapUserSeQueRepository.findAnsByUserId(activeUser.getId());
            }
        } catch(Exception e){
            System.out.println(e);
        }
        return userSecQue;
    }

    public Boolean saveInfo(){
        //This method would save the question selected by user and its answers in the DB. Have to
        // first create a type of DTO which would take list of question(and maybe its ID too) and
        // answers to those questions and save them in DB.
        return null;
    }
}
