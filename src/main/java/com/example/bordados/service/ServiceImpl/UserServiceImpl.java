package com.example.bordados.service.ServiceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.UserDTO;
import com.example.bordados.DTOs.UserResponseDTO;
import com.example.bordados.model.Discount;
import com.example.bordados.model.Role;
import com.example.bordados.model.User;
import com.example.bordados.model.UserType;
import com.example.bordados.model.Enums.DiscountType;
import com.example.bordados.model.Enums.RoleEnum;
import com.example.bordados.repository.DiscountRepository;
import com.example.bordados.repository.RoleRepository;
import com.example.bordados.repository.UserRepository;
import com.example.bordados.service.AffiliateCodeGenerator;
import com.example.bordados.service.EmailService;
import com.example.bordados.service.IUserService;

import jakarta.mail.MessagingException;

@Service
public class UserServiceImpl implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private EmailService emailService;

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
        LOGGER.info("Usuario guardado con ID: {}", user.getId());

        // Generar código de descuento único para la primera compra
        String discountCode = generateUniqueDiscountCode();
        Discount welcomeDiscount = Discount.builder()
                .code(discountCode)
                .discountPercentage(10.0)
                .type(DiscountType.SUBSCRIPTION)
                .user(user)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .maxUses(1)
                .currentUses(0)
                .build();

        try {
            discountRepository.save(welcomeDiscount);
            LOGGER.info("Descuento guardado con código: {}", welcomeDiscount.getCode());
        } catch (Exception e) {
            LOGGER.error("Error al guardar el descuento: {}", e.getMessage(), e);
            throw new RuntimeException("Error al guardar el descuento", e);
        }

        // Enviar correo de bienvenida con el código de desuento
        try {
            emailService.sendWelcomeEmail(userDTO.getEmail(), userDTO.getName(), discountCode);
        } catch (MessagingException e) {
            LOGGER.error("Error sending welcome email to: {}", userDTO.getEmail(), e);
            throw new RuntimeException("Failed to send welcome email", e);
        }

        // Manejar descuentos de afiliación si hay un código referido
        if (userDTO.getReferredAffiliateCode() != null) {
            handleAffiliateDiscount(userDTO.getReferredAffiliateCode(), user);
        }

        return UserResponseDTO.builder()
                .affiliateCode(user.getAffiliateCode())
                .address(user.getAddress())
                .email(user.getEmail())
                .name(user.getName())
                .type(user.getType())
                .id(user.getId())
                .build();
    }

    private String generateUniqueDiscountCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (discountRepository.existsByCode(code));
        return code;
    }

    private void handleAffiliateDiscount(String referredCode, User newUser) {
        // Buscar usuario referenciador
        User referrer = userRepository.findUserByAffiliateCode(referredCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de afiliación inválido"));

        // Descuento de 10% para el nuevo usuario
        Discount affiliateDiscount = Discount.builder()
                .code(UUID.randomUUID().toString()) // Código único
                .discountPercentage(10.0)
                .type(DiscountType.AFFILIATE)
                .user(newUser)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .maxUses(1)
                .build();

        // Descuento de 5% para el referenciador
        Discount referrerDiscount = Discount.builder()
                .code(UUID.randomUUID().toString())
                .discountPercentage(5.0)
                .type(DiscountType.AFFILIATE)
                .user(referrer)
                .expirationDate(LocalDateTime.now().plusMonths(1))
                .maxUses(1)
                .build();

        discountRepository.saveAll(List.of(affiliateDiscount, referrerDiscount));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        String username = authentication.getName();
        return userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void save(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existingUser.setAddress(user.getAddress());

        userRepository.save(existingUser);
    }
}
