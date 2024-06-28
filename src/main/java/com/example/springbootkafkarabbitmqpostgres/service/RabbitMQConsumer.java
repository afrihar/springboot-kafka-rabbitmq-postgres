package com.example.springbootkafkarabbitmqpostgres.service;

import com.example.springbootkafkarabbitmqpostgres.controller.SSEController;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
  private final SSEController sseController;

  public RabbitMQConsumer(SSEController sseController) {
    this.sseController = sseController;
  }

  @RabbitListener(queues = "userQueue")
  public void receiveMessage(String message) {
    System.out.println("Received message from RabbitMQ: " + message);
    sseController.sendEvent("Received from RabbitMQ: " + message);
  }
}
