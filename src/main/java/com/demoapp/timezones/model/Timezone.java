package com.demoapp.timezones.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Timezone {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String username;
    private String city;
    private int gmt;

    public static Timezone of(String name, String username, String city, int gmt) {
        Timezone t = new Timezone();
        t.setName(name);
        t.setUsername(username);
        t.setCity(city);
        t.setGmt(gmt);
        return t;
    }
}
