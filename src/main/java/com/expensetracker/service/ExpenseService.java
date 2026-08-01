package com.expensetracker.service;

import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.store.ExpenseStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseStore store;

    public ExpenseService(ExpenseStore store) {
        this.store = store;
    }

    public Expense create(String title, BigDecimal amount, String category, LocalDate date) {
        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                title,
                amount,
                category.trim(),
                date
        );
        return store.save(expense);
    }

    public List<Expense> findAll() {
        return store.findAll();
    }

    public List<Expense> findByCategory(String category) {
        String normalized = category.trim();
        return store.findAll().stream()
                .filter(e -> e.category().equalsIgnoreCase(normalized))
                .toList();
    }

    public Expense findById(String id) {
        return store.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    public void deleteById(String id) {
        boolean deleted = store.deleteById(id);
        if (!deleted) {
            throw new ExpenseNotFoundException(id);
        }
    }
    public java.util.Map<String, BigDecimal> summaryByCategory() {
        return store.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Expense::category,
                        java.util.stream.Collectors.reducing(BigDecimal.ZERO, Expense::amount, BigDecimal::add)
                ));
    }
    public java.util.Map<String, BigDecimal> monthlySummary() {
        return store.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.date().getYear() + "-" + String.format("%02d", e.date().getMonthValue()),
                        java.util.stream.Collectors.reducing(BigDecimal.ZERO, Expense::amount, BigDecimal::add)
                ));
    }

    public BigDecimal totalAmount() {
        return store.findAll().stream()
                .map(Expense::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}