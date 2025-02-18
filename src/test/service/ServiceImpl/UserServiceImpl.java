package com.example.bordados.service.ServiceImpl;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.DTOs.UserResponseDTO;
import com.example.bordados.model.Role;
import com.example.bordados.model.User;
import com.example.bordados.model.UserType;
import com.example.bordados.model.Enums.RoleEnum;
import com.example.bordados.repository.RoleRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.service.AffiliateCodeGenerator;
import com.example.bordados.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
public UserResponseDTO registerUser(UserDTO userDTO) {
    if (userRepository.findUserByEmail(userDTO.getEmail()).isPresent()) {
        LOGGER.warn("Intento de registro con correo ya existente: {}", userDTO.getEmail());
        throw new IllegalArgumentException("Email already exists");
    }

    String affiliateCode;
    do {
        affiliateCode = AffiliateCodeGenerator.generateAffiliateCode();
    } while (userRepository.findUserByAffiliateCode(affiliateCode).isPresent());

    // Buscar el rol en la base de datos
    Role defaultRole = roleRepository.findByRoleEnum(RoleEnum.USER)
            .orElseThrow(() -> new RuntimeException("Role not found"));

    User user = User.builder()
            .name(userDTO.getName())
            .type(UserType.ROLE_USER)
            .email(userDTO.getEmail())
            .registrationDate(new Date())
            .affiliateCode(affiliateCode)
            .address(userDTO.getAddress())
            .password(passwordEncoder.encode(userDTO.getPassword()))
            .credentialNoExpired(true)
            .accountNoExpired(true)
            .accountNoLocked(true)
            .isEnabled(true)
            .roles(Set.of(defaultRole))
            .build();

    user = userRepository.save(user);
    LOGGER.info("Usuario registrado exitosamente: {}", user.getEmail());

    return UserResponseDTO.builder()
            .affiliateCode(user.getAffiliateCode())
            .address(user.getAddress())
            .email(user.getEmail())
            .name(user.getName())
            .type(user.getType())
            .id(user.getId())
            .build();
}


}
