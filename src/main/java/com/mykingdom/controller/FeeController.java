package com.mykingdom.controller;

import com.mykingdom.dto.*;
import com.mykingdom.service.FeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    // ──────────────────────────────────────────────────────────
    //  FEE STRUCTURE ENDPOINTS (Admin only)
    // ──────────────────────────────────────────────────────────

    // GET /api/fees/structure - Get all fee structures
    @GetMapping("/structure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getAllFeeStructures(
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String className) {
        return ResponseEntity.ok(ApiResponse.success("Fee structures fetched",
                feeService.getAllFeeStructures(academicYear, className)));
    }

    // POST /api/fees/structure - Add new fee structure
    @PostMapping("/structure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createFeeStructure(
            @Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Fee structure created",
                feeService.createFeeStructure(request)));
    }

    // PUT /api/fees/structure/{id} - Update fee structure
    @PutMapping("/structure/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateFeeStructure(
            @PathVariable Long id,
            @Valid @RequestBody FeeStructureRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Fee structure updated",
                feeService.updateFeeStructure(id, request)));
    }

    // DELETE /api/fees/structure/{id} - Delete fee structure
    @DeleteMapping("/structure/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteFeeStructure(@PathVariable Long id) {
        feeService.deleteFeeStructure(id);
        return ResponseEntity.ok(ApiResponse.success("Fee structure deleted", null));
    }

    // ──────────────────────────────────────────────────────────
    //  FEE PAYMENT ENDPOINTS
    // ──────────────────────────────────────────────────────────

    // GET /api/fees/my - Student's own fee payments
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getMyFees(
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(ApiResponse.success("My fees fetched",
                feeService.getStudentFees(getCurrentStudentId(), academicYear)));
    }

    // GET /api/fees/my/summary - Fee summary (total, paid, pending)
    @GetMapping("/my/summary")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<ApiResponse<?>> getMyFeeSummary() {
        return ResponseEntity.ok(ApiResponse.success("Fee summary fetched",
                feeService.getStudentFeeSummary(getCurrentStudentId())));
    }

    // GET /api/fees/student/{studentId} - Admin: get any student's fees
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<?>>> getStudentFees(
            @PathVariable String studentId,
            @RequestParam(required = false) String academicYear) {
        return ResponseEntity.ok(ApiResponse.success("Student fees fetched",
                feeService.getStudentFees(studentId, academicYear)));
    }

    // POST /api/fees/pay - Record a payment (Admin)
    @PostMapping("/pay")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> recordPayment(
            @Valid @RequestBody FeePaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment recorded",
                feeService.recordPayment(request)));
    }

    // PATCH /api/fees/payment/{id}/status - Update payment status
    @PatchMapping("/payment/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Payment status updated",
                feeService.updatePaymentStatus(id, status)));
    }

    // GET /api/fees/overdue - Get all overdue payments (Admin)
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<?>>> getOverduePayments() {
        return ResponseEntity.ok(ApiResponse.success("Overdue payments fetched",
                feeService.getOverduePayments()));
    }

    // GET /api/fees/receipt/{receiptNo} - Get receipt by number
    @GetMapping("/receipt/{receiptNo}")
    @PreAuthorize("hasAnyRole('STUDENT','ADMIN')")
    public ResponseEntity<ApiResponse<?>> getReceipt(@PathVariable String receiptNo) {
        return ResponseEntity.ok(ApiResponse.success("Receipt fetched",
                feeService.getReceiptByNo(receiptNo)));
    }

    // Utility - get current student ID from security context
    private String getCurrentStudentId() {
        // In real app, extract from JWT claims / security context
        return "ST1023";
    }
}
