package com.investbank.dealpipeline.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "deals")
public class Deal {
    
    @Id
    private String id;
    
    @Indexed
    private String clientName;
    
    private String dealType;
    
    private String sector;
    
    private Long dealValue;
    
    @Indexed
    private DealStage currentStage;
    
    private String summary;
    
    @Builder.Default
    private List<Note> notes = new ArrayList<>();
    
    private String createdBy;
    
    private String assignedTo;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
