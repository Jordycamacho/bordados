package com.example.bordados.service;

import java.util.List;
import java.util.Optional;

import com.example.bordados.model.User;

public interface UserService {
    
    List<User> findAll();

    /**
     * Guarda un usuario nuevo o existente.
     * 
     * @param user Objeto User a guardar
     * @return Usuario guardado
     */
    User save(User user);

    /**
     * Actualiza un usuario existente.
     * 
     * @param user Objeto User a actualizar
     * @return Usuario actualizado
     */
    User update(User user);

    /**
     * Elimina un usuario por su ID.
     * 
     * @param idUser ID del usuario a eliminar
     */
    void deleteById(Long idUser);

    /**
     * Encuentra un usuario por su ID.
     * 
     * @param idUser ID del usuario a buscar
     * @return Optional que puede contener el usuario encontrado
     */
    Optional<User> findUserById(Long idUser);

    /**
     * Encuentra un usuario por su correo electrónico.
     * 
     * @param email Correo electrónico del usuario a buscar
     * @return Optional que puede contener el usuario encontrado
     */
    Optional<User> findUserByEmail(String email);
}
