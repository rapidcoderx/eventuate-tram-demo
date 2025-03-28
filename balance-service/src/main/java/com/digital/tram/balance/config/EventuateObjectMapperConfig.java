package com.digital.tram.balance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class EventuateObjectMapperConfig {

  private final ObjectMapper objectMapper;

  public EventuateObjectMapperConfig(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  // Static method to configure the mapper
  private static void configureMapper(ObjectMapper mapper) {
    JSonMapper.objectMapper = mapper;
  }

  @PostConstruct
  public void initialize() {
    configureMapper(objectMapper);
  }
}