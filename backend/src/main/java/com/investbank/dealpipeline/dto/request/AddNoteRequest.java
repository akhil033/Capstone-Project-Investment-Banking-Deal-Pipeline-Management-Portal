package com.investbank.dealpipeline.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddNoteRequest {
    
    @NotBlank(message = "Note cannot be empty")
    private String note;
}
