package com.hostel.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HostelExpenseTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelExpenseTrackerApplication.class, args);
    }
}
