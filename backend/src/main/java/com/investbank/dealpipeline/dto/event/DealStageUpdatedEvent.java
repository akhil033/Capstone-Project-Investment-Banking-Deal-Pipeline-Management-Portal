package com.investbank.dealpipeline.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealStageUpdatedEvent {
    
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    
    // Deal Details
    private String dealId;
    private String clientName;
    private String previousStage;
    private String newStage;
    private String updatedBy;
}
