package com.example.bordados;

/*import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.example.bordados.model.Permission;
import com.example.bordados.model.Role;
import com.example.bordados.model.RoleEnum;
import com.example.bordados.model.User;
import com.example.bordados.model.UserType;
import com.example.bordados.repository.UserRepository;
*/
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@EnableScheduling
public class BordadosApplication {

        public static void main(String[] args) {
                SpringApplication.run(BordadosApplication.class, args);
        }

        /*@Bean
        CommandLineRunner init(UserRepository userRepository) {
                return args -> {
                        // Create PERMISSIONS
                        Permission createPermission = Permission.builder()
                                        .name("CREATE")
                                        .build();

                        Permission readPermission = Permission.builder()
                                        .name("READ")
                                        .build();

                        Permission updatePermission = Permission.builder()
                                        .name("UPDATE")
                                        .build();

                        Permission deletePermission = Permission.builder()
                                        .name("DELETE")
                                        .build();

                        // Create ROLES
                        Role roleAdmin = Role.builder()
                                        .roleEnum(RoleEnum.ADMIN)
                                        .Permission(Set.of(createPermission, readPermission, updatePermission,
                                                        deletePermission))
                                        .build();

                        Role roleUser = Role.builder()
                                        .roleEnum(RoleEnum.USER)
                                        .Permission(Set.of(readPermission))
                                        .build();

                        // CREATE USERS
                        User userExample = User.builder()
                                        .name("Jordy Camacho")
                                        .email("jordycamacho225@gmail.com")
                                        .registrationDate(new Date())
                                        .password("012890")
                                        .address("Narnia")
                                        .affiliateCode("123456")
                                        .type(UserType.ROLE_ADMIN)
                                        .referrer(null)
                                        .roles(Set.of(roleAdmin))
                                        .isEnabled(true)
                                        .accountNoExpired(true)
                                        .accountNoLocked(true)
                                        .credentialNoExpired(true)
                                        .roles(Set.of(roleAdmin))
                                        .build();

                        User userexample2 = User.builder()
                                        .name("Pepe Perez")
                                        .email("example@gmail.com")
                                        .registrationDate(new Date())
                                        .password("012890")
                                        .address("Narnia")
                                        .affiliateCode("1234")
                                        .type(UserType.ROLE_ADMIN)
                                        .referrer(null)
                                        .roles(Set.of(roleAdmin))
                                        .isEnabled(true)
                                        .accountNoExpired(true)
                                        .accountNoLocked(true)
                                        .credentialNoExpired(true)
                                        .roles(Set.of(roleAdmin))
                                        .build();

                        userRepository.saveAll(List.of(userExample, userexample2));
                };
        }*/
}
