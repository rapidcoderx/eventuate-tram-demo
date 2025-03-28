package com.digital.tram.transaction;

import io.eventuate.tram.events.common.DefaultDomainEventNameMapping;
import io.eventuate.tram.events.common.DomainEventNameMapping;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration;
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.beans.factory.annotation.Value;
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
  TramEventsPublisherConfiguration.class,
  TramEventSubscriberConfiguration.class
})
public class TransactionServiceApplication {
  @Value("${balance.service.url:http://localhost:8081}")
  private String balanceServiceUrl;

  public static void main(String[] args) {
    SpringApplication.run(TransactionServiceApplication.class, args);
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder().baseUrl(balanceServiceUrl);
  }

  // Add these beans if they're not provided by imports
  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

  @Bean
  public DomainEventNameMapping domainEventNameMapping() {
    return new DefaultDomainEventNameMapping();
  }
}
