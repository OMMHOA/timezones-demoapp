package com.demoapp.timezones.controller;

import com.demoapp.timezones.responses.UserResponse;
import com.demoapp.timezones.responses.UsersResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AdminController {

    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/admin/users")
    public ResponseEntity<String> users() throws JsonProcessingException {
        UsersResponse response = new UsersResponse();
        // The SQL query of findAllGroups method is overwritten in SecurityConfig.java
        List<UserResponse> users = jdbcUserDetailsManager.findAllGroups()
                .stream()
                .map(username -> {
                    UserResponse user = new UserResponse();
                    user.setUsername(username);
                    user.setIsAdmin(Utils.isAdmin(jdbcUserDetailsManager.loadUserByUsername(username)));
                    return user;
                }).collect(Collectors.toList());
        response.setUserResponses(users);
        return ResponseEntity.ok(objectMapper.writeValueAsString(response));
    }
}
