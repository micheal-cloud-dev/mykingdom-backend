package com.mykingdom.repository;

import com.mykingdom.model.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    List<FeePayment> findByStudentStudentId(String studentId);
    List<FeePayment> findByStudentStudentIdAndFeeStructureAcademicYear(String studentId, String academicYear);
    Optional<FeePayment> findByReceiptNo(String receiptNo);

    @Query("SELECT fp FROM FeePayment fp WHERE fp.paymentStatus = 'Overdue'")
    List<FeePayment> findOverduePayments();
}
