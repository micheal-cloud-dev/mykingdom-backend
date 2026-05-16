package com.mykingdom.repository;

import com.mykingdom.model.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByIsActiveTrue();
    List<FeeStructure> findByAcademicYear(String academicYear);
    List<FeeStructure> findByAcademicYearAndClassName(String academicYear, String className);
    List<FeeStructure> findByAcademicYearAndIsActiveTrue(String academicYear);
}
