package com.lockbase.controller;

import com.lockbase.model.User;
import com.lockbase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/create_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public User createUser(@RequestBody User user){
        User new_user = null;
        try{
            new_user = userRepository.save(user);
        }catch (Exception e){
            System.out.println(e);
        }
        return new_user;
    }
}
