package com.investbank.dealpipeline.dto.request;

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
public class UpdateDealValueRequest {
    
    @NotNull(message = "Deal value is required")
    @Positive(message = "Deal value must be positive")
    private Long dealValue;
}
