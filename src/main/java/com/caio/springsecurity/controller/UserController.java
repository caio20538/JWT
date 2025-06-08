package com.caio.springsecurity.controller;

import com.caio.springsecurity.controller.dto.CreateUserDTO;
import com.caio.springsecurity.entities.UserEntity;
import com.caio.springsecurity.entities.enums.RoleEnum;
import com.caio.springsecurity.repository.RoleRepository;
import com.caio.springsecurity.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @PostMapping("/users")
    public ResponseEntity<Void> newUser(@RequestBody CreateUserDTO userDTO){
        var customer = roleRepository.findByName(RoleEnum.CUSTOMER);
        var userFromDb = userRepository.findByUsername(userDTO.username());

        if (userFromDb.isPresent())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

        var user = new UserEntity();
        user.setUserName(userDTO.username());
        user.setUserPassword(passwordEncoder.encode(userDTO.password()));
        user.setRoles(Set.of(customer));

        userRepository.save(user);

        return ResponseEntity
                .created(URI.create("/users/" + user.getUserId()))
                .build();
        //return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<UserEntity>> listUsers(){
        var users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
}
