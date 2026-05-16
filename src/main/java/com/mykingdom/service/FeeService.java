package com.mykingdom.service;

import com.mykingdom.dto.*;
import com.mykingdom.model.*;
import com.mykingdom.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeStructureRepository feeStructureRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final StudentRepository studentRepository;

    // ── Fee Structure ──────────────────────────────────

    public List<FeeStructure> getAllFeeStructures(String academicYear, String className) {
        if (academicYear != null && className != null) {
            return feeStructureRepository.findByAcademicYearAndClassName(academicYear, className);
        } else if (academicYear != null) {
            return feeStructureRepository.findByAcademicYearAndIsActiveTrue(academicYear);
        }
        return feeStructureRepository.findByIsActiveTrue();
    }

    @Transactional
    public FeeStructure createFeeStructure(FeeStructureRequest request) {
        FeeStructure fee = FeeStructure.builder()
                .feeName(request.getFeeName())
                .amount(request.getAmount())
                .category(FeeStructure.Category.valueOf(request.getCategory()))
                .frequency(FeeStructure.Frequency.valueOf(request.getFrequency()))
                .className(request.getClassName())
                .academicYear(request.getAcademicYear())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .isActive(true)
                .build();
        return feeStructureRepository.save(fee);
    }

    @Transactional
    public FeeStructure updateFeeStructure(Long id, FeeStructureRequest request) {
        FeeStructure fee = feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found with id: " + id));
        fee.setFeeName(request.getFeeName());
        fee.setAmount(request.getAmount());
        fee.setCategory(FeeStructure.Category.valueOf(request.getCategory()));
        fee.setFrequency(FeeStructure.Frequency.valueOf(request.getFrequency()));
        fee.setClassName(request.getClassName());
        fee.setAcademicYear(request.getAcademicYear());
        fee.setDueDate(request.getDueDate());
        fee.setDescription(request.getDescription());
        return feeStructureRepository.save(fee);
    }

    @Transactional
    public void deleteFeeStructure(Long id) {
        FeeStructure fee = feeStructureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found with id: " + id));
        fee.setActive(false);
        feeStructureRepository.save(fee);
    }

    // ── Fee Payments ──────────────────────────────────

    public List<FeePayment> getStudentFees(String studentId, String academicYear) {
        if (academicYear != null) {
            return feePaymentRepository
                    .findByStudentStudentIdAndFeeStructureAcademicYear(studentId, academicYear);
        }
        return feePaymentRepository.findByStudentStudentId(studentId);
    }

    public FeeSummaryResponse getStudentFeeSummary(String studentId) {
        List<FeePayment> payments = feePaymentRepository.findByStudentStudentId(studentId);

        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        int total = payments.size();
        int paid = 0;
        int pending = 0;
        int overdue = 0;

        for (FeePayment p : payments) {
            totalFees = totalFees.add(p.getFeeStructure().getAmount());
            if (p.getPaymentStatus() == FeePayment.PaymentStatus.Paid) {
                paidAmount = paidAmount.add(p.getAmountPaid());
                paid++;
            } else if (p.getPaymentStatus() == FeePayment.PaymentStatus.Overdue) {
                overdue++;
            } else {
                pending++;
            }
        }

        BigDecimal pendingAmount = totalFees.subtract(paidAmount);
        if (pendingAmount.compareTo(BigDecimal.ZERO) < 0) {
            pendingAmount = BigDecimal.ZERO;
        }

        double paidPercentage = total > 0
                ? paidAmount.divide(totalFees, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        return FeeSummaryResponse.builder()
                .totalFees(totalFees)
                .paidAmount(paidAmount)
                .pendingAmount(pendingAmount)
                .totalInstallments(total)
                .paidInstallments(paid)
                .pendingInstallments(pending)
                .overdueInstallments(overdue)
                .paidPercentage(paidPercentage)
                .build();
    }

    @Transactional
    public FeePayment recordPayment(FeePaymentRequest request) {
        Student student = studentRepository.findByStudentId(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        FeeStructure feeStructure = feeStructureRepository.findById(request.getFeeStructureId())
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));

        String receiptNo = "REC-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        FeePayment payment = FeePayment.builder()
                .student(student)
                .feeStructure(feeStructure)
                .amountPaid(request.getAmountPaid())
                .paymentMethod(FeePayment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .paymentStatus(FeePayment.PaymentStatus.Paid)
                .transactionId(request.getTransactionId())
                .receiptNo(receiptNo)
                .paidOn(request.getPaidOn() != null ? request.getPaidOn() : LocalDate.now())
                .remarks(request.getRemarks())
                .build();

        return feePaymentRepository.save(payment);
    }

    @Transactional
    public FeePayment updatePaymentStatus(Long id, String status) {
        FeePayment payment = feePaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        payment.setPaymentStatus(FeePayment.PaymentStatus.valueOf(status));
        return feePaymentRepository.save(payment);
    }

    public List<FeePayment> getOverduePayments() {
        return feePaymentRepository.findOverduePayments();
    }

    public FeePayment getReceiptByNo(String receiptNo) {
        return feePaymentRepository.findByReceiptNo(receiptNo)
                .orElseThrow(() -> new RuntimeException("Receipt not found: " + receiptNo));
    }
}
