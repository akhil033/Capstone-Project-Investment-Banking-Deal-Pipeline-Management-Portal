package com.investbank.dealpipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.investbank.dealpipeline.dto.event.DealCreatedEvent;
import com.investbank.dealpipeline.dto.event.DealStageUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    @Value("${kafka.topic.deal-events}")
    private String dealEventsTopic;
    
    
    // Publish Deal Created Event to Kafka
    public void publishDealCreatedEvent(DealCreatedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(dealEventsTopic, event.getDealId(), eventJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Deal Created Event published successfully: dealId={}, topic={}, partition={}, offset={}", 
                            event.getDealId(), 
                            dealEventsTopic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish Deal Created Event: dealId={}, error={}", 
                            event.getDealId(), ex.getMessage(), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing Deal Created Event: dealId={}, error={}", 
                    event.getDealId(), e.getMessage(), e);
        }
    }
    
    
    // Publish Deal Stage Updated Event to Kafka
    public void publishDealStageUpdatedEvent(DealStageUpdatedEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(dealEventsTopic, event.getDealId(), eventJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Deal Stage Updated Event published successfully: dealId={}, stage={}->{}, topic={}, partition={}, offset={}", 
                            event.getDealId(),
                            event.getPreviousStage(),
                            event.getNewStage(),
                            dealEventsTopic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish Deal Stage Updated Event: dealId={}, error={}", 
                            event.getDealId(), ex.getMessage(), ex);
                }
            });
            
        } catch (JsonProcessingException e) {
            log.error("Error serializing Deal Stage Updated Event: dealId={}, error={}", 
                    event.getDealId(), e.getMessage(), e);
        }
    }
}
