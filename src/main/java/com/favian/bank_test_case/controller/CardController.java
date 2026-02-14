package com.favian.bank_test_case.controller;

import com.favian.bank_test_case.dto.BalanceResponse;
import com.favian.bank_test_case.dto.CardResponse;
import com.favian.bank_test_case.dto.TransactionResponse;
import com.favian.bank_test_case.dto.TransferRequest;
import com.favian.bank_test_case.entity.User;
import com.favian.bank_test_case.entity.enums.CardStatus;
import com.favian.bank_test_case.service.CardService;
import com.favian.bank_test_case.service.TransactionService;
import com.favian.bank_test_case.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Card management endpoints")
@Validated
public class CardController {

    private final CardService cardService;
    private final TransactionService transactionService;
    private final UserService userService;

    public CardController(CardService cardService, TransactionService transactionService, UserService userService) {
        this.cardService = cardService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all cards", description = "Get all cards for the authenticated user with pagination")
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
        User currentUser = userService.getCurrentUser();
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(cardService.getUserCards(currentUser, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search cards", description = "Search cards by masked number")
    public ResponseEntity<Page<CardResponse>> searchCards(
            @Parameter(description = "Search term (card number)")
            @RequestParam @NotBlank(message = "Search query cannot be empty") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        return ResponseEntity.ok(cardService.searchUserCards(currentUser, query, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get cards by status", description = "Get all cards filtered by status")
    public ResponseEntity<Page<CardResponse>> getCardsByStatus(
            @Parameter(description = "Card status (ACTIVE, BLOCKED, EXPIRED)")
            @PathVariable CardStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        
        return ResponseEntity.ok(cardService.getUserCardsByStatus(currentUser, status, pageable));
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "Get card by ID", description = "Get specific card details")
    public ResponseEntity<CardResponse> getCardById(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(cardService.getCardById(cardId, currentUser));
    }

    @GetMapping("/{cardId}/balance")
    @Operation(summary = "Get card balance", description = "Get balance for a specific card")
    public ResponseEntity<BalanceResponse> getCardBalance(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(cardService.getCardBalance(cardId, currentUser));
    }

    @PostMapping("/{cardId}/block")
    @Operation(summary = "Block card", description = "Block a card to prevent transactions")
    public ResponseEntity<CardResponse> blockCard(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId
    ) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(cardService.blockCard(cardId, currentUser));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer between own cards", description = "Transfer money between your own cards")
    public ResponseEntity<TransactionResponse> transferBetweenOwnCards(
            @Valid @RequestBody TransferRequest request
    ) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(transactionService.transferBetweenOwnCards(request, currentUser));
    }

    @GetMapping("/{cardId}/transactions")
    @Operation(summary = "Get card transactions", description = "Get all transactions for a specific card")
    public ResponseEntity<Page<TransactionResponse>> getCardTransactions(
            @Parameter(description = "Card ID")
            @PathVariable @Positive(message = "Card ID must be positive") Long cardId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        return ResponseEntity.ok(transactionService.getCardTransactions(cardId, currentUser, pageable));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions", description = "Get all transactions for the authenticated user")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        return ResponseEntity.ok(transactionService.getUserTransactions(currentUser, pageable));
    }
}
