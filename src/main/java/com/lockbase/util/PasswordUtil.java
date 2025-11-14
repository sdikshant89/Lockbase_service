package com.lockbase.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private final PasswordEncoder encoder;

    public String hashPass(String pass){
        String pass_enc = null;
        try{
            pass_enc = encoder.encode(pass);
        }catch(Exception e){
            System.out.println(e);
        }
        return pass_enc;
    }

    public Boolean checkPass(String dbPass, String reqPass){
        try{
            return encoder.matches(reqPass, dbPass);
        }catch(Exception e){
            System.out.println(e);
        }
        return Boolean.FALSE;
    }
}
