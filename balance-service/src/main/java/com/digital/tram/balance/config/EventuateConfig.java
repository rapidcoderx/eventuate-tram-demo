package com.digital.tram.balance.config;

import io.eventuate.tram.events.common.DefaultDomainEventNameMapping;
import io.eventuate.tram.events.common.DomainEventNameMapping;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisherImpl;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.messaging.producer.MessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventuateConfig {

  @Bean
  public ChannelMapping channelMapping() {
    return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
  }

  @Bean
  public DomainEventNameMapping domainEventNameMapping() {
    return new DefaultDomainEventNameMapping();
  }

  @Bean
  public DomainEventPublisher domainEventPublisher(
      MessageProducer messageProducer, DomainEventNameMapping domainEventNameMapping) {
    return new DomainEventPublisherImpl(messageProducer, domainEventNameMapping);
  }
}
