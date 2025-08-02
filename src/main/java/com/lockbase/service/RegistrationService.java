package com.lockbase.service;

import com.lockbase.dto.UserDTO;
import com.lockbase.dto.UserResponseDTO;
import com.lockbase.exception.InternalServerException;
import com.lockbase.exception.UserAlreadyExistsException;
import com.lockbase.model.LoginUser;
import com.lockbase.repository.LoginUserRepository;
import com.lockbase.util.CryptoUtil;
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
            LoginUser savedUser = userRepository.save(user);
            return createResponse(savedUser);
        }catch (Exception e){
            throw new InternalServerException("An error occurred while registering the user.", e);
        }
    }

    public UserResponseDTO loginUser(UserDTO userDTO){
        Optional<LoginUser> user =  userRepository.findByEmail(userDTO.getEmail());

        return null;
    }

    public LoginUser populateNewUser(UserDTO userDTO) {
        try {
            // Generate salt, iv, and secure PRK
            byte[] salt = CryptoUtil.generateSalt();
            byte[] iv = CryptoUtil.generateIv();

            byte[] prkBytes = CryptoUtil.generateRandomBytes(32);
            String prkPlaintext = CryptoUtil.toBase64(prkBytes);

            String encryptedPrk = CryptoUtil.encrypt(prkPlaintext, userDTO.getPassword(), salt, iv);
            String encodedPassword = passwordUtil.encodePass(userDTO.getPassword());

            LoginUser newUser = new LoginUser();
            BeanUtils.copyProperties(userDTO, newUser);
            newUser.setCreateDate(new Timestamp(new Date().getTime()));
            newUser.setPassword(encodedPassword);
//            newUser.setSalt(CryptoUtil.toBase64(salt));
//            newUser.setIv(CryptoUtil.toBase64(iv));
//            newUser.setEncPrkPassword(encryptedPrk);
            return newUser;

        } catch (Exception e) {
            throw new InternalServerException("User registration failed during encryption step.", e);
        }
    }

    public UserResponseDTO createResponse(LoginUser user){
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createDate(user.getCreateDate()).build();
    }
}