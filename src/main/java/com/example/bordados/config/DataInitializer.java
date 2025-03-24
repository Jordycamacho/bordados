package com.example.bordados.config;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.bordados.model.Permission;
import com.example.bordados.model.Role;
import com.example.bordados.model.User;
import com.example.bordados.model.UserType;
import com.example.bordados.model.Enums.RoleEnum;
import com.example.bordados.repository.PermissionRepository;
import com.example.bordados.repository.RoleRepository;
import com.example.bordados.repository.UserRepository;


@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, 
                         PermissionRepository permissionRepository,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeRolesAndPermissions();
        createAdminUser();
    }

    private void initializeRolesAndPermissions() {
        Permission readPermission = getOrCreatePermission("READ");
        Permission writePermission = getOrCreatePermission("WRITE");
        Permission adminPermission = getOrCreatePermission("ADMIN");

        createRoleIfNotExists(RoleEnum.ADMIN, Set.of(readPermission, writePermission, adminPermission));
        createRoleIfNotExists(RoleEnum.USER, Set.of(readPermission));
        createRoleIfNotExists(RoleEnum.INVITED, Collections.emptySet());
    }

    private Permission getOrCreatePermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return permissionRepository.save(permission);
                });
    }

    private void createRoleIfNotExists(RoleEnum roleEnum, Set<Permission> permissions) {
        if (roleRepository.findByRoleEnum(roleEnum).isEmpty()) {
            Role role = new Role();
            role.setRoleEnum(roleEnum);
            role.setPermission(permissions);
            roleRepository.save(role);
        }
    }

    private void createAdminUser() {
        if (userRepository.findUserByEmail("jordycamacho225@gmail.com").isEmpty()) {
            Role adminRole = roleRepository.findByRoleEnum(RoleEnum.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("Admin role not found. Did initialization fail?"));

            User adminUser = new User();
            adminUser.setName("Jordi Camacho");
            adminUser.setEmail("jordycamacho225@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("jordy012890"));
            adminUser.setRegistrationDate(new Date());
            adminUser.setAddress("narnia");
            adminUser.setType(UserType.ROLE_ADMIN);
            adminUser.setAffiliateCode("ADMIN001");
            adminUser.setEnabled(true);
            adminUser.setAccountNoExpired(true);
            adminUser.setAccountNoLocked(true);
            adminUser.setCredentialNoExpired(true);
            adminUser.setRoles(Set.of(adminRole));
            
            userRepository.save(adminUser);
        }
    }
}