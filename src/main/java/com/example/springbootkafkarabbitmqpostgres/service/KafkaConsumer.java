package com.example.springbootkafkarabbitmqpostgres.service;

import com.example.springbootkafkarabbitmqpostgres.entity.User;
import com.example.springbootkafkarabbitmqpostgres.repo.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class KafkaConsumer {

  private final RabbitTemplate rabbitTemplate;

  private final UserRepository userRepository;

  public KafkaConsumer(UserRepository userRepository, RabbitTemplate rabbitTemplate) {
    this.userRepository = userRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  @KafkaListener(topics = "userCreate", groupId = "group_id")
  public void consume(String message) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<User> users = objectMapper.readValue(message, new TypeReference<>() {
    });
    userRepository.saveAll(users);
    rabbitTemplate.convertAndSend("userQueue", "User created successfully.");
  }

  @KafkaListener(topics = "userUpdate", groupId = "group_id")
  public void consumeUpdate(@Payload String message) {
    // message format: "update,id,name,email"
    String[] parts = message.split(",");
    String operation = parts[0];
    Long id = Long.parseLong(parts[1]);

    if ("update".equals(operation)) {
      String name = parts[2];
      String email = parts[3];
      Optional<User> optionalUser = userRepository.findById(id);
      if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);
        rabbitTemplate.convertAndSend("userQueue", "User with ID " + id + " updated successfully.");
      }
    } else if ("delete".equals(operation)) {
      Optional<User> optionalUser = userRepository.findById(id);
      if (optionalUser.isPresent()) {
        userRepository.deleteById(id);
        rabbitTemplate.convertAndSend("userQueue", "User with ID " + id + " deleted successfully.");
      }
    }
  }
}
