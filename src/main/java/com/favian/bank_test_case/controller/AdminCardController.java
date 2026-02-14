package com.favian.bank_test_case.controller;

import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.CreateCardRequest;
import com.favian.bank_test_case.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/cards")
@Tag(name = "Admin - Cards", description = "Card management endpoints for administrators")
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @Operation(summary = "Create card", description = "Create a new card for a user (Admin only)")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(request));
    }

    @GetMapping
    @Operation(summary = "Get all cards", description = "Get all cards in the system with pagination (Admin only)")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @PostMapping("/{cardId}/block")
    @Operation(summary = "Block card", description = "Block any card in the system (Admin only)")
    public ResponseEntity<CardResponse> blockCard(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        return ResponseEntity.ok(cardService.adminBlockCard(cardId));
    }

    @PostMapping("/{cardId}/activate")
    @Operation(summary = "Activate card", description = "Activate a blocked or inactive card (Admin only)")
    public ResponseEntity<CardResponse> activateCard(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @DeleteMapping("/{cardId}")
    @Operation(summary = "Delete card", description = "Delete a card from the system (Admin only)")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
}
