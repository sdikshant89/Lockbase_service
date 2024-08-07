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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
        return userSecQue;
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> saveUserSecQue(MapSecQueDTO mapSecQueDTO){
        List<MapUserSeQue> sec_que_ans = new ArrayList<>();
        Boolean save = Boolean.FALSE;
        Map<String, Object> response = new HashMap<>();
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
            response.put("saved", save);
            if(sec_que_ans.size() == 3){
                List<MapUserSeQue> result = mapUserSeQueRepository.saveAll(sec_que_ans);
                save =
                        result.stream().filter(entity -> Objects.nonNull(entity.getId())).toList().size() == 3;
                response.put("success", Boolean.TRUE);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        response.put("success", Boolean.FALSE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

//This method would save the question selected by user and its answers in the DB. Have to
// first create a type of DTO which would take list of question(and maybe its ID too) and
// answers to those questions and save them in DB.