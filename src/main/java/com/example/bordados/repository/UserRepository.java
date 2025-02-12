package com.example.bordados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bordados.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * 
     * @param email Email del usuario
     * @return Objeto Optional que puede contener el usuario encontrado
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Busca un usuario por su código de afiliado.
     * 
     * @param affiliateCode Código de afiliado del usuario
     * @return Objeto Optional que puede contener el usuario encontrado
     */
    Optional<User> findUserByAffiliateCode(String affiliateCode);
}
