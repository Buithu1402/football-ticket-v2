package com.app.footballticketservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballTicketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FootballTicketServiceApplication.class, args);
    }

}
