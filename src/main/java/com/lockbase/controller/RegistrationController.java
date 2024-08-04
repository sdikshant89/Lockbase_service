package com.lockbase.controller;


import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/register")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO registerUser(@RequestBody UserDTO user){
        return registrationService.registerUser(user);
    }
}
