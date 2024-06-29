package com.example.springbootkafkarabbitmqpostgres.service;

import com.example.springbootkafkarabbitmqpostgres.controller.SSEController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeepAliveService {

  private final SSEController sseController;

  public KeepAliveService(SSEController sseController) {
    this.sseController = sseController;
  }

  @Scheduled(fixedRate = 15000) // Send keep-alive every 15 seconds
  public void sendKeepAlive() {
    sseController.sendEvent("keep-alive");
  }
}
