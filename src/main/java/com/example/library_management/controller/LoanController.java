package com.example.library_management.controller;

import com.example.library_management.dto.ApiResponse;
import com.example.library_management.dto.loan.LoanRequest;
import com.example.library_management.dto.loan.LoanResponse;
import com.example.library_management.security.UserPrincipal;
import com.example.library_management.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> borrowBook(
            @Valid @RequestBody LoanRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LoanResponse loan = loanService.borrowBook(request, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book borrowed successfully", loan));
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<ApiResponse<LoanResponse>> returnBook(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        LoanResponse loan = loanService.returnBook(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", loan));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<LoanResponse> loans = loanService.getCurrentUserLoans(currentUser);
        return ResponseEntity.ok(ApiResponse.success(loans));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        List<LoanResponse> loans = loanService.getAllLoans();
        return ResponseEntity.ok(ApiResponse.success(loans));
    }
}
