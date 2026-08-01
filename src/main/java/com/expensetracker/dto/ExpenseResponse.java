package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        String id,
        String title,
        BigDecimal amount,
        String category,
        LocalDate date
) {}