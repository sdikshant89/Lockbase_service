package com.lockbase.service;

import com.lockbase.dto.MapSecQueDTO;
import com.lockbase.dto.SecAnsDTO;
import com.lockbase.dto.UserQueDTO;
import com.lockbase.model.MapUserSeQue;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.MapUserSeQueRepository;
import com.lockbase.repository.SecurityQuestionRepository;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SecurityQuestionService {

    @Autowired
    private PasswordUtil passwordUtil;

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

    @Transactional
    public Boolean saveUserSecQue(MapSecQueDTO mapSecQueDTO){
        List<MapUserSeQue> sec_que_ans = new ArrayList<>();
        try{
            Optional<LoginUser> user = userRepository.findById(mapSecQueDTO.getUserId());

            user.ifPresent(loginUser -> mapSecQueDTO.getQueAns().forEach(dto -> {
                Optional<SecurityQuestion> question =
                        securityQuestionRepository.findById(dto.getQuestionId());
                if (question.isPresent()) {
                    MapUserSeQue mapUserSeQue =
                            MapUserSeQue.builder()
                                    .user(loginUser)
                                    .securityQuestion(question.get())
                                    .answer(passwordUtil.encodePass(dto.getAnswer()))
                                    .build();
                    sec_que_ans.add(mapUserSeQue);
                }
            }));
            if(sec_que_ans.size() == 3){
                List<MapUserSeQue> result = mapUserSeQueRepository.saveAll(sec_que_ans);
                return result.stream().filter(entity -> Objects.nonNull(entity.getId())).toList().size() == 3;
            }
        }catch(Exception e){
            System.out.println(e);
        }
        return false;
    }
}

//This method would save the question selected by user and its answers in the DB. Have to
// first create a type of DTO which would take list of question(and maybe its ID too) and
// answers to those questions and save them in DB.