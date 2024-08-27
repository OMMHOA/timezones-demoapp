package com.demoapp.timezones.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimezoneRequest {
    private String name;
    private String city;
    private Integer gmt;
}
