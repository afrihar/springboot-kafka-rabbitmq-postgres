package com.example.springbootkafkarabbitmqpostgres.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQProducer {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  public void sendMessage(String queueName, String message) {
    rabbitTemplate.convertAndSend(queueName, message);
  }
}
