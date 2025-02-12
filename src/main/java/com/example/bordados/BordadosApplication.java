package com.example.bordados;

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

                        
                };
        }*/
}
