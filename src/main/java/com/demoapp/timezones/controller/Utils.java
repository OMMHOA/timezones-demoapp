package com.demoapp.timezones.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demoapp.timezones.responses.ProblemResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.stream.Collectors;

class Utils {
    private Utils() {}
    static boolean isAdmin(UserDetails user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
                .contains("ROLE_ADMIN");
    }

    static ResponseEntity<String> getUserNameNotFoundResponse(String userName, ObjectMapper objectMapper) throws JsonProcessingException {
        ProblemResponse problemResponse = new ProblemResponse();
        problemResponse.setTitle("Username not found");
        problemResponse.setDetail("The provided username " + userName + " is not found.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(objectMapper.writeValueAsString(problemResponse));
    }
}
