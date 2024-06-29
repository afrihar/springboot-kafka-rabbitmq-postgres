package com.example.springbootkafkarabbitmqpostgres.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@CrossOrigin(origins = "*")
public class SSEController {
  private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  @GetMapping("/events")
  public SseEmitter getEvents() {
    SseEmitter emitter = new SseEmitter();
    emitters.add(emitter);
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    return emitter;
  }

  public void sendEvent(String message) {
    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(message, MediaType.TEXT_PLAIN);
      } catch (IOException e) {
        emitters.remove(emitter);
      }
    }
  }
}
