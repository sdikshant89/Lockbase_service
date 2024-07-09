package com.lockbase.controller;

import com.lockbase.dto.Titles;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/lockbase/register")
public class RegistrationController {

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(value = "/get_titles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Titles getTitlesforRegistration(){
        Titles titles = new Titles();
        List<String> nameTitles = new ArrayList<>();
        nameTitles.add("Mr");
        nameTitles.add("Mrs");
        nameTitles.add("Ms");
        titles.setTitle(nameTitles);
        return titles;
    }
}
