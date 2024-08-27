package com.demoapp.timezones.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProblemResponse {
    private String title;
    private String detail;
}
