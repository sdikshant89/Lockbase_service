package com.lockbase.service;

import com.lockbase.model.User;
import com.lockbase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user){
        // Implement fetching user by email
        // Create DTO and pass create date from here not from frontend
        User new_user = null;
        try{
            new_user = userRepository.save(user);
        }catch (Exception e){
            System.out.println(e);
        }
        return new_user;
    }
}
