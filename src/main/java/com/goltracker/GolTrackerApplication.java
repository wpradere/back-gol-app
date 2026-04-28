package com.goltracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GolTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GolTrackerApplication.class, args);
    }
}
