package com.investbank.dealpipeline.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDealRequest {
    
    private String clientName;
    private String dealType;
    private String sector;
    private String summary;
    private String assignedTo;
}
