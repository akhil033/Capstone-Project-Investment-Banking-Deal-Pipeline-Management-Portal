package com.investbank.dealpipeline.dto.response;

import com.investbank.dealpipeline.model.DealStage;
import com.investbank.dealpipeline.model.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealResponse {
    private String id;
    private String clientName;
    private String dealType;
    private String sector;
    private Long dealValue;
    private DealStage currentStage;
    private String summary;
    private List<Note> notes;
    private String createdBy;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
