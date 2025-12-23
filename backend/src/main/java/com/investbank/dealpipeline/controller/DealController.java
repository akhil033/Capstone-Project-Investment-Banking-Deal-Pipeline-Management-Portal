package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.dto.request.AddNoteRequest;
import com.investbank.dealpipeline.dto.request.CreateDealRequest;
import com.investbank.dealpipeline.dto.request.UpdateDealRequest;
import com.investbank.dealpipeline.dto.request.UpdateDealStageRequest;
import com.investbank.dealpipeline.dto.request.UpdateDealValueRequest;
import com.investbank.dealpipeline.dto.response.DealResponse;
import com.investbank.dealpipeline.dto.response.DealSummaryResponse;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import com.investbank.dealpipeline.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {
    
    private final DealService dealService;
    private final UserRepository userRepository;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DealResponse> createDeal(
            @Valid @RequestBody CreateDealRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.createDeal(request, user.getId(), user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DealResponse>> getAllDeals(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        List<DealResponse> deals = dealService.getAllDeals(user.getRole());
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<DealSummaryResponse>> getAllDealsSummary() {
        List<DealSummaryResponse> deals = dealService.getAllDealsSummary();
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DealResponse> getDealById(
            @PathVariable String id,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.getDealById(id, user.getRole());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DealResponse> updateDeal(
            @PathVariable String id,
            @RequestBody UpdateDealRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.updateDeal(id, request, user.getRole());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/stage")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DealResponse> updateDealStage(
            @PathVariable String id,
            @Valid @RequestBody UpdateDealStageRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.updateDealStage(id, request.getStage(), user.getRole());
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{id}/value")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DealResponse> updateDealValue(
            @PathVariable String id,
            @Valid @RequestBody UpdateDealValueRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.updateDealValue(id, request.getDealValue(), user.getRole());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<DealResponse> addNote(
            @PathVariable String id,
            @Valid @RequestBody AddNoteRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        
        DealResponse response = dealService.addNote(id, request, user.getId(), user.getRole());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDeal(@PathVariable String id) {
        dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }
}
