package com.investbank.dealpipeline.mapper;

import com.investbank.dealpipeline.dto.response.DealResponse;
import com.investbank.dealpipeline.dto.response.DealSummaryResponse;
import com.investbank.dealpipeline.model.Deal;
import com.investbank.dealpipeline.model.Role;
import org.springframework.stereotype.Component;

@Component
public class DealMapper {
    
    public DealResponse toResponse(Deal deal, Role userRole) {
        if (deal == null) {
            return null;
        }
        
        DealResponse.DealResponseBuilder builder = DealResponse.builder()
                .id(deal.getId())
                .clientName(deal.getClientName())
                .dealType(deal.getDealType())
                .sector(deal.getSector())
                .currentStage(deal.getCurrentStage())
                .summary(deal.getSummary())
                .notes(deal.getNotes())
                .createdBy(deal.getCreatedBy())
                .assignedTo(deal.getAssignedTo())
                .createdAt(deal.getCreatedAt())
                .updatedAt(deal.getUpdatedAt());
        
        // Only include deal value for ADMIN role
        if (userRole == Role.ADMIN) {
            builder.dealValue(deal.getDealValue());
        }
        
        return builder.build();
    }
    
    public DealSummaryResponse toSummaryResponse(Deal deal) {
        if (deal == null) {
            return null;
        }
        
        return DealSummaryResponse.builder()
                .id(deal.getId())
                .clientName(deal.getClientName())
                .dealType(deal.getDealType())
                .sector(deal.getSector())
                .currentStage(deal.getCurrentStage())
                .summary(deal.getSummary())
                .assignedTo(deal.getAssignedTo())
                .updatedAt(deal.getUpdatedAt())
                .build();
    }
}
