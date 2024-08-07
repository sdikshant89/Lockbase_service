package com.lockbase.controller;

import com.lockbase.dto.MapSecQueDTO;
import com.lockbase.dto.SecAnsDTO;
import com.lockbase.dto.UserQueDTO;
import com.lockbase.model.SecurityQuestion;
import com.lockbase.service.SecurityQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lockbase/SecQue")
public class SecurityQuestionController {

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/get_all_questions", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecurityQuestion> findAllQuestions(){
        return securityQuestionService.findAllQuestions();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/save_user_sec_que", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> saveUserSecQue(@RequestBody MapSecQueDTO mapSecQueDTO){
        return securityQuestionService.saveUserSecQue(mapSecQueDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/get_user_que", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecAnsDTO> getUserSecQue(@RequestBody UserQueDTO userQueDTO){
        return securityQuestionService.getUserSecQue(userQueDTO);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/validate_sec_ans", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SecAnsDTO> validateUserSecQue(@RequestBody UserQueDTO userQueDTO){
        return securityQuestionService.getUserSecQue(userQueDTO);
    }
}
