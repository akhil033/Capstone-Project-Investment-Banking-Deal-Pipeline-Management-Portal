package com.investbank.dealpipeline.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    
    
     // Kafka consumer that listens to deal-events topic and logs all events
     // This demonstrates the event-driven architecture for deal lifecycle tracking
     
    @KafkaListener(topics = "${kafka.topic.deal-events}", groupId = "${kafka.consumer.group-id}")
    public void consumeDealEvent(String eventPayload) {
        log.info("--------------------------------");
        log.info("Received Deal Event from Kafka:");
        log.info("Event Payload: {}", eventPayload);
        
        // Additional processing can be added here:
        // - Send notifications
        // - Update analytics dashboards
        // - Trigger workflows
        // - Audit logging to separate database
        // - Integration with external systems
    }
}
