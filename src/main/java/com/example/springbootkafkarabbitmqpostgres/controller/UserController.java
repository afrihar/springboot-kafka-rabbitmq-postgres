package com.example.springbootkafkarabbitmqpostgres.controller;

import com.example.springbootkafkarabbitmqpostgres.entity.User;
import com.example.springbootkafkarabbitmqpostgres.repo.UserRepository;
import com.example.springbootkafkarabbitmqpostgres.service.KafkaProducer;
import com.example.springbootkafkarabbitmqpostgres.service.RabbitMQProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private KafkaProducer kafkaProducer;

  @Autowired
  private RabbitMQProducer rabbitMQProducer;

  @Autowired
  private UserRepository userRepository;

  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody List<User> users) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String message = objectMapper.writeValueAsString(users);

    // Publish to Kafka
    kafkaProducer.sendMessage("userTopic", message);

    // Send success message to RabbitMQ
    rabbitMQProducer.sendMessage("userQueue", "User creation received by Kafka");

    return ResponseEntity.ok("User creation request processed");
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      user.setName(userDetails.getName());
      user.setEmail(userDetails.getEmail());
      userRepository.save(user);
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      userRepository.deleteById(id);
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
