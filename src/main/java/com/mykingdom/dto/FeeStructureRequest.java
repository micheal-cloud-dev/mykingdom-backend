package com.mykingdom.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureRequest {

    @NotBlank(message = "Fee name is required")
    @Size(max = 100)
    private String feeName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    private String className;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    private LocalDate dueDate;
    private String description;
}
