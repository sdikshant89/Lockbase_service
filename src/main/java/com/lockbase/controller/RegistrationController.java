package com.lockbase.controller;


import com.lockbase.dto.UserDTO;
import com.lockbase.model.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/register")
public class RegistrationController {

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public LoginUser registerUser(@RequestBody UserDTO user){
        return userService.createUser(user);
    }
}
