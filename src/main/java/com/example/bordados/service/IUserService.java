package com.example.bordados.service;

import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.DTOs.UserResponseDTO;

public interface IUserService {

    UserResponseDTO registerUser(UserDTO userDTO);
    
    /**List<User> findAll();

    User save(User user);

    User update(User user);
   
    void deleteById(Long idUser);
    
    Optional<User> findUserById(Long idUser);
   
    Optional<User> findUserByEmail(String email);

    */
}
