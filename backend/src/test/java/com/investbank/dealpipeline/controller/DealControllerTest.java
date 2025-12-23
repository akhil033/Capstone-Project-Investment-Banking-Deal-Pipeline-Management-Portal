package com.investbank.dealpipeline.controller;

import com.investbank.dealpipeline.TestApplication;
import com.investbank.dealpipeline.dto.request.CreateDealRequest;
import com.investbank.dealpipeline.dto.request.UpdateDealRequest;
import com.investbank.dealpipeline.dto.response.DealResponse;
import com.investbank.dealpipeline.dto.response.DealSummaryResponse;
import com.investbank.dealpipeline.model.DealStage;
import com.investbank.dealpipeline.model.Role;
import com.investbank.dealpipeline.model.User;
import com.investbank.dealpipeline.repository.UserRepository;
import com.investbank.dealpipeline.security.JwtTokenProvider;
import com.investbank.dealpipeline.security.UserDetailsServiceImpl;
import com.investbank.dealpipeline.service.DealService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DealController.class,
    properties = "spring.main.banner-mode=off")
@ContextConfiguration(classes = TestApplication.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DealService dealService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateDealSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        CreateDealRequest request = CreateDealRequest.builder()
                .clientName("ABC Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage(DealStage.Prospect)
                .summary("Tech acquisition deal")
                .assignedTo("test@bank.com")
                .build();

        DealResponse response = DealResponse.builder()
                .id("deal123")
                .clientName("ABC Corp")
                .dealType("M&A")
                .sector("Technology")
                .currentStage(DealStage.Prospect)
                .summary("Tech acquisition deal")
                .assignedTo("test@bank.com")
                .build();

        when(dealService.createDeal(any(CreateDealRequest.class), anyString(), any(Role.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/deals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("deal123"))
                .andExpect(jsonPath("$.clientName").value("ABC Corp"))
                .andExpect(jsonPath("$.dealType").value("M&A"))
                .andExpect(jsonPath("$.currentStage").value("Prospect"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetAllDealsSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        DealResponse deal1 = DealResponse.builder()
                .id("deal1")
                .clientName("Client1")
                .dealType("M&A")
                .currentStage(DealStage.Prospect)
                .build();

        DealResponse deal2 = DealResponse.builder()
                .id("deal2")
                .clientName("Client2")
                .dealType("IPO")
                .currentStage(DealStage.UnderEvaluation)
                .build();

        List<DealResponse> deals = Arrays.asList(deal1, deal2);
        when(dealService.getAllDeals(any(Role.class)))
                .thenReturn(deals);

        mockMvc.perform(get("/api/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clientName").value("Client1"))
                .andExpect(jsonPath("$[1].clientName").value("Client2"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldGetDealByIdSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        DealResponse deal = DealResponse.builder()
                .id("deal123")
                .clientName("Test Client")
                .dealType("IPO")
                .currentStage(DealStage.UnderEvaluation)
                .build();

        when(dealService.getDealById(eq("deal123"), any(Role.class)))
                .thenReturn(deal);

        mockMvc.perform(get("/api/deals/deal123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("deal123"))
                .andExpect(jsonPath("$.clientName").value("Test Client"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetDealsSummarySuccessfully() throws Exception {
        DealSummaryResponse summary = DealSummaryResponse.builder()
                .id("deal1")
                .clientName("Summary Client")
                .currentStage(DealStage.TermSheetSubmitted)
                .build();

        List<DealSummaryResponse> summaries = Arrays.asList(summary);
        when(dealService.getAllDealsSummary())
                .thenReturn(summaries);

        mockMvc.perform(get("/api/deals/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clientName").value("Summary Client"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldUpdateDealSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        UpdateDealRequest request = UpdateDealRequest.builder()
                .clientName("Updated Client")
                .dealType("M&A")
                .sector("Finance")
                .summary("Updated summary")
                .build();

        DealResponse response = DealResponse.builder()
                .id("deal123")
                .clientName("Updated Client")
                .dealType("M&A")
                .sector("Finance")
                .currentStage(DealStage.UnderEvaluation)
                .build();

        when(dealService.updateDeal(eq("deal123"), any(UpdateDealRequest.class), any(Role.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/deals/deal123")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientName").value("Updated Client"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldUpdateDealStageSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        DealResponse response = DealResponse.builder()
                .id("deal123")
                .clientName("Test Client")
                .currentStage(DealStage.TermSheetSubmitted)
                .build();

        when(dealService.updateDealStage(eq("deal123"), eq(DealStage.TermSheetSubmitted), any(Role.class)))
                .thenReturn(response);

        String requestBody = "{\"stage\":\"TermSheetSubmitted\"}";

        mockMvc.perform(patch("/api/deals/deal123/stage")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStage").value("TermSheetSubmitted"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateDealValueSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("admin123");
        mockUser.setUsername("admin");
        mockUser.setRole(Role.ADMIN);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        
        DealResponse response = DealResponse.builder()
                .id("deal123")
                .clientName("Test Client")
                .dealValue(2000000L)
                .build();

        when(dealService.updateDealValue(eq("deal123"), eq(2000000L), any(Role.class)))
                .thenReturn(response);

        String requestBody = "{\"dealValue\":2000000}";

        mockMvc.perform(patch("/api/deals/deal123/value")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealValue").value(2000000));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldAddNoteSuccessfully() throws Exception {
        User mockUser = new User();
        mockUser.setId("user123");
        mockUser.setUsername("user");
        mockUser.setRole(Role.USER);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        
        DealResponse response = DealResponse.builder()
                .id("deal123")
                .clientName("Test Client")
                .build();

        when(dealService.addNote(eq("deal123"), any(), eq("user123"), any(Role.class)))
                .thenReturn(response);

        String requestBody = "{\"note\":\"Important update on deal progress\"}";

        mockMvc.perform(post("/api/deals/deal123/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("deal123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteDealSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/deals/deal123")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(dealService).deleteDeal("deal123");
    }
}
