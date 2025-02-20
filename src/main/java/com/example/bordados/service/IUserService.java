package com.example.bordados.service;

import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.DTOs.UserResponseDTO;
import com.example.bordados.model.User;

public interface IUserService {

    UserResponseDTO registerUser(UserDTO userDTO);
    
    User getCurrentUser();
    /**List<User> findAll();

    User save(User user);

    User update(User user);
   
    void deleteById(Long idUser);
    
    Optional<User> findUserById(Long idUser);
   
    Optional<User> findUserByEmail(String email);

    */
}
