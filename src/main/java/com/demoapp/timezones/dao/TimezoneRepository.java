package com.demoapp.timezones.dao;

import com.demoapp.timezones.model.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimezoneRepository extends JpaRepository<Timezone, Long> {
    List<Timezone> findByUsername(String username);
    List<Timezone> findByNameAndUsername(String name, String username);
    boolean existsByNameAndUsername(String name, String username);
    void deleteAllByNameAndUsername(String name, String username);
}
