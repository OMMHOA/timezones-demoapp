package com.demoapp.timezones.responses;

import lombok.Data;

import java.util.List;

@Data
public class UsersResponse {
    private List<UserResponse> userResponses;
}
