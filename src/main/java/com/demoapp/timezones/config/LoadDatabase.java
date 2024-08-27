package com.demoapp.timezones.config;

import com.demoapp.timezones.dao.TimezoneRepository;
import com.demoapp.timezones.model.Timezone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(TimezoneRepository repository) {
        return args -> {
            if (!repository.existsByNameAndUsername("Abroad", "asd")) {
                log.info("Preloading " + repository.save(Timezone.of("Abroad", "asd", "Helsinki", 3)));
            }
            if (!repository.existsByNameAndUsername("Home", "asd")) {
                log.info("Preloading " + repository.save(Timezone.of("Home", "asd", "Vienna", 2)));
            }
        };
    }
}
