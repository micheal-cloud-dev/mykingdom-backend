package com.mykingdom.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeePaymentRequest {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Fee structure ID is required")
    private Long feeStructureId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal amountPaid;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String transactionId;
    private LocalDate paidOn;
    private String remarks;
}
