package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final LoginUserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public UserResponseDTO registerUser(UserDTO userDTO){
        Optional<LoginUser> exists = userRepository.findByEmail(userDTO.getEmail());
        if (exists.isPresent()) {
            throw new UserAlreadyExistsException("User with the username and email already exists, try logging in.");
        }
        try{
            LoginUser user = populateNewUser(userDTO);
            return createResponse(userRepository.save(user));
        }catch (Exception e){
            throw new InternalServerException("An error occurred while registering the user.", e);
        }
    }

    public UserResponseDTO loginUser(UserDTO userDTO){
        Optional<LoginUser> user =  userRepository.findByEmail(userDTO.getEmail());

        return null;
    }

    public LoginUser populateNewUser(UserDTO userDTO){
        LoginUser new_user = new LoginUser();
        BeanUtils.copyProperties(userDTO, new_user);

        Date date = new Date();
        new_user.setCreateDate(new Timestamp(date.getTime()));
        new_user.setPassword(passwordUtil.encodePass(userDTO.getPassword()));
        return new_user;
    }

    public UserResponseDTO createResponse(LoginUser user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createDate(user.getCreateDate()).build();
    }
}