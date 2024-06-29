package com.example.springbootkafkarabbitmqpostgres.controller;

import com.example.springbootkafkarabbitmqpostgres.entity.User;
import com.example.springbootkafkarabbitmqpostgres.repo.UserRepository;
import com.example.springbootkafkarabbitmqpostgres.service.KafkaProducer;
import com.example.springbootkafkarabbitmqpostgres.service.RabbitMQProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

  private final KafkaProducer kafkaProducer;

  private final RabbitMQProducer rabbitMQProducer;

  private final UserRepository userRepository;

  private final KafkaTemplate<String, String> kafkaTemplate;

  public UserController(KafkaProducer kafkaProducer, RabbitMQProducer rabbitMQProducer, UserRepository userRepository, KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaProducer = kafkaProducer;
    this.rabbitMQProducer = rabbitMQProducer;
    this.userRepository = userRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody List<User> users) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String message = objectMapper.writeValueAsString(users);

    // Publish to Kafka
    kafkaProducer.sendMessage("userCreate", message);

    // Send success message to RabbitMQ
    rabbitMQProducer.sendMessage("userQueue", "User creation received by Kafka");

    return ResponseEntity.ok("User creation request processed");
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUser(@PathVariable Long id) {
    Optional<User> user = userRepository.findById(id);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<List<User>> getUsers() {
    List<User> users = userRepository.findAll();
    return ResponseEntity.ok(users);
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      kafkaTemplate.send("userUpdate", "update," + id + "," + userDetails.getName() + "," + userDetails.getEmail());
      return ResponseEntity.ok(optionalUser.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      kafkaTemplate.send("userUpdate", "delete," + id);
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
