package com.investbank.dealpipeline.service;

import com.investbank.dealpipeline.dto.event.DealCreatedEvent;
import com.investbank.dealpipeline.dto.event.DealStageUpdatedEvent;
import com.investbank.dealpipeline.dto.request.AddNoteRequest;
import com.investbank.dealpipeline.dto.request.CreateDealRequest;
import com.investbank.dealpipeline.dto.request.UpdateDealRequest;
import com.investbank.dealpipeline.dto.response.DealResponse;
import com.investbank.dealpipeline.dto.response.DealSummaryResponse;
import com.investbank.dealpipeline.exception.BadRequestException;
import com.investbank.dealpipeline.exception.ResourceNotFoundException;
import com.investbank.dealpipeline.mapper.DealMapper;
import com.investbank.dealpipeline.model.Deal;
import com.investbank.dealpipeline.model.DealStage;
import com.investbank.dealpipeline.model.Note;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.repository.DealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService {
    
    private final DealRepository dealRepository;
    private final DealMapper dealMapper;
    private final KafkaProducerService kafkaProducerService;
    
    @Transactional
    public DealResponse createDeal(CreateDealRequest request, String userId, Role userRole) {
        log.debug("Creating new deal for client: {}", request.getClientName());
        
        Deal deal = Deal.builder()
                .clientName(request.getClientName())
                .dealType(request.getDealType())
                .sector(request.getSector())
                .dealValue(request.getDealValue())
                .currentStage(request.getCurrentStage())
                .summary(request.getSummary())
                .notes(new ArrayList<>())
                .createdBy(userId)
                .assignedTo(request.getAssignedTo() != null ? request.getAssignedTo() : userId)
                .createdAt(LocalDateTime.now())
                .build();
        
        Deal savedDeal = dealRepository.save(deal);
        
        // Publish Deal Created Event to Kafka
        DealCreatedEvent event = DealCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DEAL_CREATED")
                .timestamp(LocalDateTime.now())
                .dealId(savedDeal.getId())
                .clientName(savedDeal.getClientName())
                .dealType(savedDeal.getDealType())
                .sector(savedDeal.getSector())
                .dealValue(savedDeal.getDealValue())
                .currentStage(savedDeal.getCurrentStage().name())
                .summary(savedDeal.getSummary())
                .createdBy(savedDeal.getCreatedBy())
                .assignedTo(savedDeal.getAssignedTo())
                .build();
        
        kafkaProducerService.publishDealCreatedEvent(event);
        
        return dealMapper.toResponse(savedDeal, userRole);
    }
    
    public List<DealResponse> getAllDeals(Role userRole) {
        log.debug("Fetching all deals");
        
        return dealRepository.findAll().stream()
                .map(deal -> dealMapper.toResponse(deal, userRole))
                .collect(Collectors.toList());
    }
    
    public List<DealSummaryResponse> getAllDealsSummary() {
        log.debug("Fetching all deals summary");
        
        return dealRepository.findAll().stream()
                .map(dealMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }
    
    public DealResponse getDealById(String id, Role userRole) {
        log.debug("Fetching deal by ID: {}", id);
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        return dealMapper.toResponse(deal, userRole);
    }
    
    @Transactional
    public DealResponse updateDeal(String id, UpdateDealRequest request, Role userRole) {
        log.debug("Updating deal: {}", id);
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        if (request.getClientName() != null) {
            deal.setClientName(request.getClientName());
        }
        if (request.getDealType() != null) {
            deal.setDealType(request.getDealType());
        }
        if (request.getSector() != null) {
            deal.setSector(request.getSector());
        }
        if (request.getSummary() != null) {
            deal.setSummary(request.getSummary());
        }
        if (request.getAssignedTo() != null) {
            deal.setAssignedTo(request.getAssignedTo());
        }
        
        deal.setUpdatedAt(LocalDateTime.now());
        Deal updatedDeal = dealRepository.save(deal);
        
        return dealMapper.toResponse(updatedDeal, userRole);
    }
    
    @Transactional
    public DealResponse updateDealStage(String id, DealStage stage, Role userRole) {
        log.debug("Updating deal stage: {} to {}", id, stage);
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        DealStage previousStage = deal.getCurrentStage();
        deal.setCurrentStage(stage);
        deal.setUpdatedAt(LocalDateTime.now());
        Deal updatedDeal = dealRepository.save(deal);
        
        // Publish Deal Stage Updated Event to Kafka
        DealStageUpdatedEvent event = DealStageUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DEAL_STAGE_UPDATED")
                .timestamp(LocalDateTime.now())
                .dealId(updatedDeal.getId())
                .clientName(updatedDeal.getClientName())
                .previousStage(previousStage.name())
                .newStage(stage.name())
                .updatedBy("system") // Can be enhanced to track actual user
                .build();
        
        kafkaProducerService.publishDealStageUpdatedEvent(event);
        
        return dealMapper.toResponse(updatedDeal, userRole);
    }
    
    @Transactional
    public DealResponse updateDealValue(String id, Long dealValue, Role userRole) {
        log.debug("Updating deal value: {} to {}", id, dealValue);
        
        if (userRole != Role.ADMIN) {
            throw new BadRequestException("Only ADMIN can update deal value");
        }
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        deal.setDealValue(dealValue);
        deal.setUpdatedAt(LocalDateTime.now());
        Deal updatedDeal = dealRepository.save(deal);
        
        return dealMapper.toResponse(updatedDeal, userRole);
    }
    
    @Transactional
    public DealResponse addNote(String id, AddNoteRequest request, String userId, Role userRole) {
        log.debug("Adding note to deal: {}", id);
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        Note note = Note.builder()
                .userId(userId)
                .note(request.getNote())
                .timestamp(LocalDateTime.now())
                .build();
        
        deal.getNotes().add(note);
        deal.setUpdatedAt(LocalDateTime.now());
        Deal updatedDeal = dealRepository.save(deal);
        
        return dealMapper.toResponse(updatedDeal, userRole);
    }
    
    @Transactional
    public void deleteDeal(String id) {
        log.debug("Deleting deal: {}", id);
        
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        
        dealRepository.delete(deal);
    }
    
    public List<DealResponse> getDealsByStage(DealStage stage, Role userRole) {
        log.debug("Fetching deals by stage: {}", stage);
        
        return dealRepository.findByCurrentStage(stage).stream()
                .map(deal -> dealMapper.toResponse(deal, userRole))
                .collect(Collectors.toList());
    }
}
