package com.investbank.dealpipeline.dto.request;

import com.investbank.dealpipeline.model.DealStage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDealStageRequest {
    
    @NotNull(message = "Deal stage is required")
    private DealStage stage;
}
