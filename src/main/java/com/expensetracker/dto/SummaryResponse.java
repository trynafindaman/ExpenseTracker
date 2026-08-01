package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.Map;

public record SummaryResponse(
        BigDecimal total,
        Map<String, BigDecimal> byCategory
) {}