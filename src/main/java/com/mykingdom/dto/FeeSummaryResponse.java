package com.mykingdom.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSummaryResponse {
    private BigDecimal totalFees;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private int totalInstallments;
    private int paidInstallments;
    private int pendingInstallments;
    private int overdueInstallments;
    private double paidPercentage;
}
