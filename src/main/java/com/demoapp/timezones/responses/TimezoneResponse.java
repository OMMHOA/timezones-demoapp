package com.demoapp.timezones.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimezoneResponse {
    private String name;
    private String city;
    private Integer gmt;
    private String time;
}
