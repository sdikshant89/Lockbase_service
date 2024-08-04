package com.lockbase.controller;

import com.lockbase.dto.UserDTO;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/user")
public class UserController {

    @Autowired
    private LoginUserRepository userRepository;

    @Autowired
    private UserService userService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/create_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginUser createUser(@RequestBody UserDTO user){
        return userService.createUser(user);
    }
}
