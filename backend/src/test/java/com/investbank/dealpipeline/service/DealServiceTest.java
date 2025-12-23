package com.investbank.dealpipeline.service;

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
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.repository.DealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
    
    @Mock
    private DealRepository dealRepository;
    
    @Mock
    private DealMapper dealMapper;
    
    @InjectMocks
    private DealService dealService;
    
    private CreateDealRequest createDealRequest;
    private UpdateDealRequest updateDealRequest;
    private AddNoteRequest addNoteRequest;
    private Deal deal;
    private DealResponse dealResponse;
    private DealSummaryResponse dealSummaryResponse;
    
    @BeforeEach
    void setUp() {
        createDealRequest = CreateDealRequest.builder()
                .clientName("Acme Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage(DealStage.Prospect)
                .summary("Test deal")
                .build();
        
        updateDealRequest = UpdateDealRequest.builder()
                .clientName("Updated Corp")
                .build();
        
        addNoteRequest = AddNoteRequest.builder()
                .note("Test note")
                .build();
        
        deal = Deal.builder()
                .id("deal123")
                .clientName("Acme Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage(DealStage.Prospect)
                .summary("Test deal")
                .notes(new ArrayList<>())
                .createdBy("user123")
                .assignedTo("user123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        dealResponse = DealResponse.builder()
                .id("deal123")
                .clientName("Acme Corp")
                .dealType("M&A")
                .sector("Technology")
                .currentStage(DealStage.Prospect)
                .summary("Test deal")
                .notes(new ArrayList<>())
                .createdBy("user123")
                .assignedTo("user123")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        dealSummaryResponse = DealSummaryResponse.builder()
                .id("deal123")
                .clientName("Acme Corp")
                .dealType("M&A")
                .sector("Technology")
                .currentStage(DealStage.Prospect)
                .summary("Test deal")
                .assignedTo("user123")
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void createDeal_Success() {
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.createDeal(createDealRequest, "user123", Role.USER);
        
        assertNotNull(result);
        assertEquals("Acme Corp", result.getClientName());
        verify(dealRepository, times(1)).save(any(Deal.class));
    }
    
    @Test
    void getAllDeals_Success() {
        List<Deal> deals = Arrays.asList(deal);
        when(dealRepository.findAll()).thenReturn(deals);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        List<DealResponse> result = dealService.getAllDeals(Role.USER);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dealRepository, times(1)).findAll();
    }
    
    @Test
    void getAllDealsSummary_Success() {
        List<Deal> deals = Arrays.asList(deal);
        when(dealRepository.findAll()).thenReturn(deals);
        when(dealMapper.toSummaryResponse(any(Deal.class))).thenReturn(dealSummaryResponse);
        
        List<DealSummaryResponse> result = dealService.getAllDealsSummary();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dealRepository, times(1)).findAll();
    }
    
    @Test
    void getDealById_Success() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.getDealById("deal123", Role.USER);
        
        assertNotNull(result);
        assertEquals("deal123", result.getId());
        verify(dealRepository, times(1)).findById("deal123");
    }
    
    @Test
    void getDealById_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.getDealById("deal123", Role.USER));
    }
    
    @Test
    void updateDeal_Success() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.updateDeal("deal123", updateDealRequest, Role.USER);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }
    
    @Test
    void updateDealStage_Success() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.updateDealStage("deal123", DealStage.Closed, Role.USER);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }
    
    @Test
    void updateDealValue_Success_AdminRole() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.updateDealValue("deal123", 2000000L, Role.ADMIN);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }
    
    @Test
    void updateDealValue_UserRole_ThrowsException() {
        assertThrows(BadRequestException.class, () -> 
                dealService.updateDealValue("deal123", 2000000L, Role.USER));
        
        verify(dealRepository, never()).save(any(Deal.class));
    }
    
    @Test
    void addNote_Success() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.addNote("deal123", addNoteRequest, "user123", Role.USER);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }
    
    @Test
    void deleteDeal_Success() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        doNothing().when(dealRepository).delete(any(Deal.class));
        
        dealService.deleteDeal("deal123");
        
        verify(dealRepository, times(1)).delete(any(Deal.class));
    }
    
    @Test
    void getDealsByStage_Success() {
        List<Deal> deals = Arrays.asList(deal);
        when(dealRepository.findByCurrentStage(any(DealStage.class))).thenReturn(deals);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        List<DealResponse> result = dealService.getDealsByStage(DealStage.Prospect, Role.USER);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(dealRepository, times(1)).findByCurrentStage(DealStage.Prospect);
    }

    @Test
    void updateDeal_WithAllFields_Success() {
        UpdateDealRequest fullRequest = UpdateDealRequest.builder()
                .clientName("Updated Client")
                .dealType("IPO")
                .sector("Finance")
                .summary("Updated summary")
                .assignedTo("user456")
                .build();

        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.updateDeal("deal123", fullRequest, Role.USER);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }

    @Test
    void updateDeal_WithNullFields_Success() {
        UpdateDealRequest partialRequest = UpdateDealRequest.builder()
                .clientName("Updated Client")
                .build();

        when(dealRepository.findById(anyString())).thenReturn(Optional.of(deal));
        when(dealRepository.save(any(Deal.class))).thenReturn(deal);
        when(dealMapper.toResponse(any(Deal.class), any(Role.class))).thenReturn(dealResponse);
        
        DealResponse result = dealService.updateDeal("deal123", partialRequest, Role.USER);
        
        assertNotNull(result);
        verify(dealRepository, times(1)).save(any(Deal.class));
    }

    @Test
    void updateDeal_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.updateDeal("deal123", updateDealRequest, Role.USER));
    }

    @Test
    void updateDealStage_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.updateDealStage("deal123", DealStage.Closed, Role.USER));
    }

    @Test
    void updateDealValue_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.updateDealValue("deal123", 2000000L, Role.ADMIN));
    }

    @Test
    void addNote_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.addNote("deal123", addNoteRequest, "user123", Role.USER));
    }

    @Test
    void deleteDeal_NotFound_ThrowsException() {
        when(dealRepository.findById(anyString())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> 
                dealService.deleteDeal("deal123"));
    }

    @Test
    void getAllDeals_EmptyList_Success() {
        when(dealRepository.findAll()).thenReturn(Arrays.asList());
        
        List<DealResponse> result = dealService.getAllDeals(Role.USER);
        
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(dealRepository, times(1)).findAll();
    }

    @Test
    void getAllDealsSummary_MultipleDeals_Success() {
        Deal deal2 = Deal.builder()
                .id("deal456")
                .clientName("Beta Corp")
                .dealType("IPO")
                .build();
        
        DealSummaryResponse summary2 = DealSummaryResponse.builder()
                .id("deal456")
                .clientName("Beta Corp")
                .build();

        when(dealRepository.findAll()).thenReturn(Arrays.asList(deal, deal2));
        when(dealMapper.toSummaryResponse(deal)).thenReturn(dealSummaryResponse);
        when(dealMapper.toSummaryResponse(deal2)).thenReturn(summary2);
        
        List<DealSummaryResponse> result = dealService.getAllDealsSummary();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(dealRepository, times(1)).findAll();
        verify(dealMapper, times(2)).toSummaryResponse(any(Deal.class));
    }

    @Test
    void getDealsByStage_EmptyList_Success() {
        when(dealRepository.findByCurrentStage(any(DealStage.class))).thenReturn(Arrays.asList());
        
        List<DealResponse> result = dealService.getDealsByStage(DealStage.Closed, Role.USER);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
