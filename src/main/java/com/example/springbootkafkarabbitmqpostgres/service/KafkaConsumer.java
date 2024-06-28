package com.example.springbootkafkarabbitmqpostgres.service;

import com.example.springbootkafkarabbitmqpostgres.entity.User;
import com.example.springbootkafkarabbitmqpostgres.repo.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class KafkaConsumer {

  private final UserRepository userRepository;

  public KafkaConsumer(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @KafkaListener(topics = "userTopic", groupId = "group_id")
  public void consume(String message) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<User> users = objectMapper.readValue(message, new TypeReference<>() {
    });
    userRepository.saveAll(users);
  }
}
