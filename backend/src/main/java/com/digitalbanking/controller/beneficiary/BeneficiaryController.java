package com.digitalbanking.controller.beneficiary;

import com.digitalbanking.dto.beneficiary.*;
import com.digitalbanking.dto.common.ApiResponse;
import com.digitalbanking.service.beneficiary.BeneficiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
@Tag(name = "Beneficiaries", description = "Beneficiary management endpoints")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    @Operation(summary = "Add beneficiary")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse response = beneficiaryService.addBeneficiary(request);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary added", response));
    }

    @GetMapping
    @Operation(summary = "Get my beneficiaries")
    public ResponseEntity<ApiResponse<List<BeneficiaryResponse>>> getMyBeneficiaries() {
        List<BeneficiaryResponse> beneficiaries = beneficiaryService.getMyBeneficiaries();
        return ResponseEntity.ok(ApiResponse.success(beneficiaries));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete beneficiary")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok(ApiResponse.success("Beneficiary deleted", null));
    }
}
