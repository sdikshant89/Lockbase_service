package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private LoginUserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    public UserResponseDTO registerUser(UserDTO userDTO){
        LoginUser new_user = new LoginUser();
        LoginUser exists = userRepository.findUserByEmailAndUsername(userDTO.getEmail(), userDTO.getUsername());

        if(Objects.isNull(exists)){
            try{
                BeanUtils.copyProperties(userDTO, new_user);

                Date date = new Date();
                new_user.setCreateDate(new Timestamp(date.getTime()));
                new_user.setPassword(passwordUtil.encodePass(userDTO.getPassword()));

                return createResponse(userRepository.save(new_user));
            }catch (Exception e){
                System.out.println(e);
            }
        }
        return null;
    }

    public UserResponseDTO loginUser(UserDTO userDTO){
        return userRepository.findByUsername(userDTO.getUsername())
                .map(this::createResponse)  // Transform the found user to a response DTO
                .orElse(null);
    }

    public UserResponseDTO createResponse(LoginUser user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createDate(user.getCreateDate()).build();
    }
}