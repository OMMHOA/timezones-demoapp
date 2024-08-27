package com.demoapp.timezones.requests;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String password;
    private Boolean isAdmin;
}
