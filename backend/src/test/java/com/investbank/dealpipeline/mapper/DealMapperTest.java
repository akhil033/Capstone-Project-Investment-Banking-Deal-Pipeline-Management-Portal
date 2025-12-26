package com.investbank.dealpipeline.mapper;

import com.investbank.dealpipeline.dto.response.DealResponse;
import com.investbank.dealpipeline.dto.response.DealSummaryResponse;
import com.investbank.dealpipeline.model.Deal;
import com.investbank.dealpipeline.model.DealStage;
import com.investbank.dealpipeline.model.Note;
import com.investbank.dealpipeline.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DealMapperTest {

    private DealMapper dealMapper;

    @BeforeEach
    void setUp() {
        dealMapper = new DealMapper();
    }

    @Test
    void shouldMapDealToResponseForAdminRole() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<Note> notes = Arrays.asList(
                Note.builder().userId("user1").note("Note 1").timestamp(now).build(),
                Note.builder().userId("user2").note("Note 2").timestamp(now).build()
        );

        Deal deal = Deal.builder()
                .id("deal123")
                .clientName("ABC Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage(DealStage.Prospect)
                .summary("Tech acquisition deal")
                .notes(notes)
                .createdBy("admin@bank.com")
                .assignedTo("banker@bank.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        DealResponse response = dealMapper.toResponse(deal, Role.ADMIN);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("deal123");
        assertThat(response.getClientName()).isEqualTo("ABC Corp");
        assertThat(response.getDealType()).isEqualTo("M&A");
        assertThat(response.getSector()).isEqualTo("Technology");
        assertThat(response.getDealValue()).isEqualTo(1000000L);
        assertThat(response.getCurrentStage()).isEqualTo(DealStage.Prospect);
        assertThat(response.getSummary()).isEqualTo("Tech acquisition deal");
        assertThat(response.getNotes()).hasSize(2);
        assertThat(response.getCreatedBy()).isEqualTo("admin@bank.com");
        assertThat(response.getAssignedTo()).isEqualTo("banker@bank.com");
    }

    @Test
    void shouldIncludeDealValueForUserRole() {
        // Given
        Deal deal = Deal.builder()
                .id("deal123")
                .clientName("ABC Corp")
                .dealType("M&A")
                .sector("Technology")
                .dealValue(1000000L)
                .currentStage(DealStage.Prospect)
                .summary("Tech acquisition deal")
                .notes(new ArrayList<>())
                .createdBy("admin@bank.com")
                .assignedTo("banker@bank.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        DealResponse response = dealMapper.toResponse(deal, Role.USER);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDealValue()).isEqualTo(1000000L);
        assertThat(response.getClientName()).isEqualTo("ABC Corp");
    }

    @Test
    void shouldReturnNullWhenDealIsNull() {
        // When
        DealResponse response = dealMapper.toResponse(null, Role.ADMIN);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void shouldMapDealToSummaryResponse() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Deal deal = Deal.builder()
                .id("deal456")
                .clientName("XYZ Ltd")
                .dealType("IPO")
                .sector("Finance")
                .dealValue(5000000L)
                .currentStage(DealStage.UnderEvaluation)
                .summary("IPO preparation")
                .notes(new ArrayList<>())
                .createdBy("admin@bank.com")
                .assignedTo("banker@bank.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        DealSummaryResponse response = dealMapper.toSummaryResponse(deal);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("deal456");
        assertThat(response.getClientName()).isEqualTo("XYZ Ltd");
        assertThat(response.getDealType()).isEqualTo("IPO");
        assertThat(response.getSector()).isEqualTo("Finance");
        assertThat(response.getCurrentStage()).isEqualTo(DealStage.UnderEvaluation);
        assertThat(response.getSummary()).isEqualTo("IPO preparation");
        assertThat(response.getAssignedTo()).isEqualTo("banker@bank.com");
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldReturnNullWhenDealIsNullForSummary() {
        // When
        DealSummaryResponse response = dealMapper.toSummaryResponse(null);

        // Then
        assertThat(response).isNull();
    }

    @Test
    void shouldHandleEmptyNotesList() {
        // Given
        Deal deal = Deal.builder()
                .id("deal789")
                .clientName("Test Corp")
                .dealType("Advisory")
                .sector("Healthcare")
                .dealValue(2000000L)
                .currentStage(DealStage.TermSheetSubmitted)
                .summary("Healthcare advisory")
                .notes(new ArrayList<>())
                .createdBy("admin@bank.com")
                .assignedTo("banker@bank.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        DealResponse response = dealMapper.toResponse(deal, Role.ADMIN);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getNotes()).isEmpty();
    }

    @Test
    void shouldMapAllDealStages() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        DealStage[] stages = {
                DealStage.Prospect,
                DealStage.UnderEvaluation,
                DealStage.TermSheetSubmitted,
                DealStage.Closed,
                DealStage.Lost
        };

        for (DealStage stage : stages) {
            Deal deal = Deal.builder()
                    .id("deal-" + stage)
                    .clientName("Client")
                    .dealType("M&A")
                    .sector("Tech")
                    .dealValue(1000000L)
                    .currentStage(stage)
                    .summary("Summary")
                    .notes(new ArrayList<>())
                    .createdBy("admin@bank.com")
                    .assignedTo("banker@bank.com")
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            // When
            DealResponse response = dealMapper.toResponse(deal, Role.ADMIN);

            // Then
            assertThat(response.getCurrentStage()).isEqualTo(stage);
        }
    }
}
