package com.frank.shortiy.shortify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ShortifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortifyApplication.class, args);
    }

}
