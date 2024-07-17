package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.model.User;
import com.lockbase.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(UserDTO userDTO){
        User new_user = new User();
        User exists = userRepository.findUserByEmail(userDTO.getEmail());
        if(Objects.isNull(exists)){
            try{
                BeanUtils.copyProperties(userDTO, new_user);

                Date date = new Date();
                new_user.setCreateDate(new Timestamp(date.getTime()));

                return userRepository.save(new_user);
            }catch (Exception e){
                System.out.println(e);
            }
        }
        return null;
    }
}
