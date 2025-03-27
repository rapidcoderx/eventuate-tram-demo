package com.digital.tram.transaction;

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@Import({
        TramMessageProducerJdbcConfiguration.class,
        TramConsumerJdbcAutoConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class,
        TramEventsPublisherConfiguration.class
})
public class TransactionServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(TransactionServiceApplication.class, args);
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}