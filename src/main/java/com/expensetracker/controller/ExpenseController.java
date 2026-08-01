package com.expensetracker.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        Expense created = service.create(
                request.title(),
                request.amount(),
                request.category(),
                request.date()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public List<ExpenseResponse> findAll(@RequestParam(required = false) String category) {
        List<Expense> expenses = (category == null)
                ? service.findAll()
                : service.findByCategory(category);

        return expenses.stream().map(this::toResponse).toList();
    }

    @GetMapping("/summary/monthly")
    public java.util.Map<String, BigDecimal> monthlySummary() {
        return service.monthlySummary();
    }

    @GetMapping("/{id}")
    public ExpenseResponse findById(@PathVariable String id) {
        return toResponse(service.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/summary")
    public SummaryResponse summary() {
        return new SummaryResponse(service.totalAmount(), service.summaryByCategory());
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.id(),
                expense.title(),
                expense.amount(),
                expense.category(),
                expense.date()
        );
    }
}