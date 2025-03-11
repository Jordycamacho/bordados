package com.example.bordados.service;

import java.util.Optional;

import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.DTOs.UserResponseDTO;
import com.example.bordados.model.User;

public interface IUserService {

    UserResponseDTO registerUser(UserDTO userDTO);
    
    User getCurrentUser();

    Optional<User> findUserByEmail(String email);
    
    void save(User user);

}
