package com.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Expense(
        String id,
        String title,
        BigDecimal amount,
        String category,
        LocalDate date
) {}