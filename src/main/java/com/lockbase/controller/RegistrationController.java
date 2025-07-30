package com.lockbase.controller;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lockbase/register")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/register_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO user){
        UserResponseDTO response = registrationService.registerUser(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(value = "/login_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> loginUser(@RequestBody UserDTO user){
        Object response = registrationService.loginUser(user);
        if (response == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
