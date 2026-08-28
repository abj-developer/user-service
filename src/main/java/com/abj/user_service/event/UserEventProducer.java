package com.abj.user_service.event;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private static final String TOPIC = "user-created";

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserEventProducer(
            KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreated(UserCreatedEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getUserId().toString(),
                event
        );
    }
}