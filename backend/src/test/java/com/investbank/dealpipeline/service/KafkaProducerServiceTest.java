package com.investbank.dealpipeline.service;

import com.investbank.dealpipeline.dto.event.DealCreatedEvent;
import com.investbank.dealpipeline.dto.event.DealStageUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {
    
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @InjectMocks
    private KafkaProducerService kafkaProducerService;
    
    private static final String TOPIC = "deal-events";
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaProducerService, "dealEventsTopic", TOPIC);
    }
    
    @Test
    void shouldPublishDealCreatedEvent() {
        // Given
        DealCreatedEvent event = DealCreatedEvent.builder()
                .eventId("event-123")
                .eventType("DEAL_CREATED")
                .timestamp(LocalDateTime.now())
                .dealId("deal-123")
                .clientName("Acme Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage("Prospect")
                .summary("Tech acquisition")
                .createdBy("user-123")
                .assignedTo("banker-123")
                .build();
        
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(eq(TOPIC), eq("deal-123"), any(String.class))).thenReturn(future);
        
        // When
        kafkaProducerService.publishDealCreatedEvent(event);
        
        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        
        assertThat(topicCaptor.getValue()).isEqualTo(TOPIC);
        assertThat(keyCaptor.getValue()).isEqualTo("deal-123");
        assertThat(valueCaptor.getValue()).contains("DEAL_CREATED");
        assertThat(valueCaptor.getValue()).contains("Acme Corp");
    }
    
    @Test
    void shouldPublishDealStageUpdatedEvent() {
        // Given
        DealStageUpdatedEvent event = DealStageUpdatedEvent.builder()
                .eventId("event-456")
                .eventType("DEAL_STAGE_UPDATED")
                .timestamp(LocalDateTime.now())
                .dealId("deal-123")
                .clientName("Acme Corp")
                .previousStage("Prospect")
                .newStage("UnderEvaluation")
                .updatedBy("banker-123")
                .build();
        
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(eq(TOPIC), eq("deal-123"), any(String.class))).thenReturn(future);
        
        // When
        kafkaProducerService.publishDealStageUpdatedEvent(event);
        
        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        
        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        
        assertThat(topicCaptor.getValue()).isEqualTo(TOPIC);
        assertThat(keyCaptor.getValue()).isEqualTo("deal-123");
        assertThat(valueCaptor.getValue()).contains("DEAL_STAGE_UPDATED");
        assertThat(valueCaptor.getValue()).contains("Prospect");
        assertThat(valueCaptor.getValue()).contains("UnderEvaluation");
    }
    
    @Test
    void shouldHandleKafkaPublishingException() {
        // Given
        DealCreatedEvent event = DealCreatedEvent.builder()
                .eventId("event-789")
                .eventType("DEAL_CREATED")
                .timestamp(LocalDateTime.now())
                .dealId("deal-789")
                .clientName("Test Corp")
                .build();
        
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));
        
        when(kafkaTemplate.send(eq(TOPIC), eq("deal-789"), any(String.class))).thenReturn(future);
        
        // When
        kafkaProducerService.publishDealCreatedEvent(event);
        
        // Then
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq("deal-789"), any(String.class));
        // Service should log error but not throw exception
    }
}
