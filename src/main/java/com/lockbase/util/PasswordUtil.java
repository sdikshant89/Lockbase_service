package com.lockbase.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final PasswordEncoder encoder;

    public PasswordUtil() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public String encodePass(String pass){
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
