package com.investbank.dealpipeline.dto.response;

import com.investbank.dealpipeline.model.DealStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealSummaryResponse {
    private String id;
    private String clientName;
    private String dealType;
    private String sector;
    private DealStage currentStage;
    private String summary;
    private String assignedTo;
    private LocalDateTime updatedAt;
}
