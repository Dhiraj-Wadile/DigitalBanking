package com.digitalbanking.controller.card;

import com.digitalbanking.dto.card.*;
import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.service.card.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Cards", description = "Card management endpoints")
public class CardController {

    private final CardService cardService;

    @PostMapping("/issue")
    @Operation(summary = "Issue new card")
    public ResponseEntity<ApiResponse<CardResponse>> issueCard(@Valid @RequestBody CardRequest request) {
        CardResponse response = cardService.issueCard(request);
        return ResponseEntity.ok(ApiResponse.success("Card issued", response));
    }

    @GetMapping
    @Operation(summary = "Get my cards")
    public ResponseEntity<ApiResponse<List<CardResponse>>> getMyCards() {
        List<CardResponse> cards = cardService.getMyCards();
        return ResponseEntity.ok(ApiResponse.success(cards));
    }

    @PostMapping("/{cardId}/block")
    @Operation(summary = "Block card")
    public ResponseEntity<ApiResponse<CardResponse>> blockCard(@PathVariable Long cardId) {
        CardResponse response = cardService.blockCard(cardId);
        return ResponseEntity.ok(ApiResponse.success("Card blocked", response));
    }

    @PostMapping("/{cardId}/unblock")
    @Operation(summary = "Unblock card")
    public ResponseEntity<ApiResponse<CardResponse>> unblockCard(@PathVariable Long cardId) {
        CardResponse response = cardService.unblockCard(cardId);
        return ResponseEntity.ok(ApiResponse.success("Card unblocked", response));
    }

    @PutMapping("/{cardId}/controls")
    @Operation(summary = "Update card controls")
    public ResponseEntity<ApiResponse<Void>> updateControls(
            @PathVariable Long cardId,
            @RequestParam(required = false) Boolean online,
            @RequestParam(required = false) Boolean international,
            @RequestParam(required = false) Boolean tapToPay,
            @RequestParam(required = false) Boolean atm,
            @RequestParam(required = false) Boolean pos) {
        cardService.updateCardControls(cardId, online, international, tapToPay, atm, pos);
        return ResponseEntity.ok(ApiResponse.success("Card controls updated", null));
    }
}
