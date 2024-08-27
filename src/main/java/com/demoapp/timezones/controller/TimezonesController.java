package com.demoapp.timezones.controller;

import com.demoapp.timezones.requests.TimezoneRequest;
import com.demoapp.timezones.responses.TimezoneResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.demoapp.timezones.dao.TimezoneRepository;
import com.demoapp.timezones.model.Timezone;
import com.demoapp.timezones.responses.ProblemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TimezonesController {
    @Autowired
    JdbcUserDetailsManager jdbcUserDetailsManager;

    private final TimezoneRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TimezonesController(TimezoneRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/timezones/{userName}")
    public ResponseEntity<String> timezones(@PathVariable String userName, @AuthenticationPrincipal User user) throws JsonProcessingException {
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

        Instant instant = Instant.now();
        List<TimezoneResponse> timezones = repository.findByUsername(userName)
                .stream().map(timezone -> {
                    OffsetDateTime time = instant.atOffset(buildZoneOffset(timezone.getGmt()));
                    return new TimezoneResponse(timezone.getName(), timezone.getCity(), timezone.getGmt(), time.toString());
                }).collect(Collectors.toList());
        return ResponseEntity.ok(objectMapper.writeValueAsString(timezones));
    }

    private static ZoneOffset buildZoneOffset(int gmt) {
        StringBuilder builder = new StringBuilder();
        if (gmt > 0) {
            builder.append("+");
        }
        if (gmt < 0) {
            builder.append("-");
        }
        if (Math.abs(gmt) < 10) {
            builder.append("0");
        }
        builder.append(Math.abs(gmt));
        builder.append(":00");
        return ZoneOffset.of(builder.toString());
    }

    @PostMapping("/timezones/{userName}")
    public ResponseEntity<String> addTimezone(@PathVariable String userName, @AuthenticationPrincipal User user,
                                              @RequestBody TimezoneRequest body) throws JsonProcessingException {
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

        if (Objects.isNull(body.getName()) || Objects.isNull(body.getCity()) || Objects.isNull(body.getGmt())) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Invalid payload");
            problemResponse.setDetail("The payload has to contain name (string), city (string) and gmt (int) non-null fields!");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        if (repository.existsByNameAndUsername(body.getName(), userName)) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Invalid payload");
            problemResponse.setDetail("The timezone name already exists for user!");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        repository.save(Timezone.of(body.getName(), userName, body.getCity(), body.getGmt()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/timezones/{userName}/{name}")
    public ResponseEntity<String> update(@PathVariable String userName, @PathVariable String name,
                                         @AuthenticationPrincipal User user, @RequestBody TimezoneRequest body) throws JsonProcessingException {
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

        if (!repository.existsByNameAndUsername(name, userName)) {
            ProblemResponse problemResponse = new ProblemResponse();
            problemResponse.setTitle("Invalid payload");
            problemResponse.setDetail("The timezone " + name + " for user " + userName + " doesn't exist.");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(objectMapper.writeValueAsString(problemResponse));
        }

        Timezone timezoneInDb = repository.findByNameAndUsername(name, userName).get(0);
        String city = Optional.ofNullable(body.getCity()).orElse(timezoneInDb.getCity());
        Integer gmt = Optional.ofNullable(body.getGmt()).orElse(timezoneInDb.getGmt());
        timezoneInDb.setCity(city);
        timezoneInDb.setGmt(gmt);
        repository.save(timezoneInDb);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/timezones/{userName}/{name}")
    public ResponseEntity<String> delete(@PathVariable String userName, @PathVariable String name,
                                         @AuthenticationPrincipal User user) throws JsonProcessingException {
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

        if (repository.existsByNameAndUsername(name, userName)) {
            Timezone timezoneInDb = repository.findByNameAndUsername(name, userName).get(0);
            repository.delete(timezoneInDb);
        }
        return ResponseEntity.ok().build();
    }
}
