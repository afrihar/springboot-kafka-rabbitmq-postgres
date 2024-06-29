package com.example.springbootkafkarabbitmqpostgres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootKafkaRabbitmqPostgresApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootKafkaRabbitmqPostgresApplication.class, args);
  }

}
