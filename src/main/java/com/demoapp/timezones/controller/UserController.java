package com.demoapp.timezones.controller;

import com.demoapp.timezones.dao.TimezoneRepository;
import com.demoapp.timezones.requests.UserUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demoapp.timezones.requests.UserRequest;
import com.demoapp.timezones.responses.ProblemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

@RestController
public class UserController {
    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final TimezoneRepository repository;

    public UserController(TimezoneRepository repository) {
        this.repository = repository;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/user/registration")
    public ResponseEntity<String> registration(@RequestBody UserRequest userRequest) throws IOException {
        if (jdbcUserDetailsManager.userExists(userRequest.getUsername())) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("User already exists");
            problemResponse.setDetail("The provided username " + userRequest.getUsername() + " already exists.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(objectMapper.writeValueAsString(problemResponse));
        }

        UserDetails user = User.withUsername(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .roles("USER")
                .build();
        jdbcUserDetailsManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/{userName}")
    public ResponseEntity<String> update(@PathVariable String userName, @AuthenticationPrincipal User user,
                                         @RequestBody UserUpdateRequest body) throws JsonProcessingException {
        boolean principalIsAdmin = Utils.isAdmin(user);
        boolean principalIsUser = !principalIsAdmin;

        if (principalIsUser && !user.getUsername().equals(userName)) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Forbidden operation");
            problemResponse.setDetail("User has no rights to edit data of user " + userName + ".");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        if (!jdbcUserDetailsManager.userExists(userName)) {
            return Utils.getUserNameNotFoundResponse(userName, objectMapper);
        }

        if (principalIsUser && Objects.nonNull(body.getIsAdmin()) && body.getIsAdmin()) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Forbidden operation");
            problemResponse.setDetail("User has no rights to change isAdmin status.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        UserDetails userFromDb = jdbcUserDetailsManager.loadUserByUsername(userName);

        String password = Optional.ofNullable(body.getPassword()).map(passwordEncoder::encode).orElse(userFromDb.getPassword());
        Boolean isAdmin = Optional.ofNullable(body.getIsAdmin()).orElse(Utils.isAdmin(userFromDb));

        User.UserBuilder userBuilder = User.withUsername(userName)
                .password(password)
                .roles("USER");
        if (isAdmin) {
            userBuilder = userBuilder.roles("ADMIN");
        }
        jdbcUserDetailsManager.updateUser(userBuilder.build());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{userName}")
    public ResponseEntity<String> delete(@PathVariable String userName, @AuthenticationPrincipal User user) throws JsonProcessingException {
        boolean principalIsAdmin = Utils.isAdmin(user);
        boolean principalIsUser = !principalIsAdmin;

        if (principalIsUser && !user.getUsername().equals(userName)) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Forbidden operation");
            problemResponse.setDetail("User has no rights to edit data of user " + userName + ".");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        if (!jdbcUserDetailsManager.userExists(userName)) {
            return Utils.getUserNameNotFoundResponse(userName, objectMapper);
        }

        repository.findByUsername(userName).forEach(repository::delete);

        jdbcUserDetailsManager.deleteUser(userName);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/user/hello")
    public String hello(Principal principal) {
        return principal.getName();
    }

    @RequestMapping("/admin/hello")
    public String helloAdmin(Principal principal) {
        return principal.getName();
    }
}
