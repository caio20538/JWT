package com.caio.springsecurity.config;

import com.caio.springsecurity.entities.RoleEntity;
import com.caio.springsecurity.entities.UserEntity;
import com.caio.springsecurity.entities.enums.RoleEnum;
import com.caio.springsecurity.repository.RoleRepository;
import com.caio.springsecurity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class AdminConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminConfig(RoleRepository repository,
                       UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = repository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        var roleAdmin = roleRepository.findByName(RoleEnum.ADMIN);

        var userAdmin = userRepository.findByUsername("ADMIN");

        userAdmin.ifPresentOrElse(
                userEntity -> {
                    System.out.println("Admin já existe");
                },
                () -> {
                    var user = new UserEntity();
                    user.setUserName("admin");
                    user.setUserPassword(passwordEncoder.encode("123"));
                    user.setRoles(Set.of(roleAdmin));
                    userRepository.save(user);
                }
        );
    }
}
