package com.ideaworks.club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Ideaworks1Application {

    public static void main(String[] args) {
        SpringApplication.run(Ideaworks1Application.class, args);
    }

}
