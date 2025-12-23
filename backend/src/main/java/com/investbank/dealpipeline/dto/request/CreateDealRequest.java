package com.investbank.dealpipeline.dto.request;

import com.investbank.dealpipeline.model.DealStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDealRequest {
    
    @NotBlank(message = "Client name is required")
    private String clientName;
    
    @NotBlank(message = "Deal type is required")
    private String dealType;
    
    @NotBlank(message = "Sector is required")
    private String sector;
    
    @NotNull(message = "Deal value is required")
    @Positive(message = "Deal value must be positive")
    private Long dealValue;
    
    @NotNull(message = "Current stage is required")
    private DealStage currentStage;
    
    private String summary;
    
    private String assignedTo;
}
