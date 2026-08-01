package com.expensetracker.service;

import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.model.Expense;
import com.expensetracker.store.ExpenseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseServiceTest {

    private ExpenseService service;
    private FakeExpenseStore fakeStore;

    @BeforeEach
    void setUp() {
        fakeStore = new FakeExpenseStore();
        service = new ExpenseService(fakeStore);
    }

    @Test
    void create_generatesId_andTrimsCategory_andSaves() {
        Expense created = service.create("Groceries", new BigDecimal("45.30"), "  Food  ", LocalDate.of(2026, 6, 5));

        assertNotNull(created.id());
        assertEquals("Food", created.category());
        assertEquals(1, fakeStore.findAll().size());
    }

    @Test
    void findAll_returnsAllExpenses() {
        service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));
        service.create("Bus pass", new BigDecimal("20.00"), "Transport", LocalDate.of(2026, 6, 6));

        List<Expense> all = service.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void findByCategory_matchesCaseInsensitively() {
        service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));
        service.create("Bus pass", new BigDecimal("20.00"), "Transport", LocalDate.of(2026, 6, 6));

        List<Expense> found = service.findByCategory("food");

        assertEquals(1, found.size());
        assertEquals("Groceries", found.get(0).title());
    }

    @Test
    void findById_returnsExpense_whenExists() {
        Expense created = service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));

        Expense found = service.findById(created.id());

        assertEquals(created, found);
    }

    @Test
    void findById_throws_whenIdDoesNotExist() {
        assertThrows(ExpenseNotFoundException.class, () -> service.findById("nonexistent-id"));
    }

    @Test
    void deleteById_removesExpense_whenExists() {
        Expense created = service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));

        service.deleteById(created.id());

        assertTrue(service.findAll().isEmpty());
    }

    @Test
    void summary_returnsZero_whenNoExpenses() {
        assertEquals(BigDecimal.ZERO, service.totalAmount());
        assertTrue(service.summaryByCategory().isEmpty());
    }

    @Test
    void summary_returnsCorrectTotalsAndByCategory() {
        service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));
        service.create("Lunch", new BigDecimal("10.00"), "Food", LocalDate.of(2026, 6, 6));
        service.create("Bus pass", new BigDecimal("20.00"), "Transport", LocalDate.of(2026, 6, 7));

        BigDecimal total = service.totalAmount();
        java.util.Map<String, BigDecimal> byCategory = service.summaryByCategory();

        assertEquals(new BigDecimal("75.30"), total);
        assertEquals(new BigDecimal("55.30"), byCategory.get("Food"));
        assertEquals(new BigDecimal("20.00"), byCategory.get("Transport"));
    }
    @Test
    void monthlySummary_groupsByYearMonth() {
        service.create("Groceries", new BigDecimal("45.30"), "Food", LocalDate.of(2026, 6, 5));
        service.create("Lunch", new BigDecimal("10.00"), "Food", LocalDate.of(2026, 6, 20));
        service.create("Rent", new BigDecimal("500.00"), "Housing", LocalDate.of(2026, 7, 1));

        java.util.Map<String, BigDecimal> result = service.monthlySummary();

        assertEquals(new BigDecimal("55.30"), result.get("2026-06"));
        assertEquals(new BigDecimal("500.00"), result.get("2026-07"));
    }

    @Test
    void monthlySummary_returnsEmptyMap_whenNoExpenses() {
        assertTrue(service.monthlySummary().isEmpty());
    }

    @Test
    void deleteById_throws_whenIdDoesNotExist() {
        assertThrows(ExpenseNotFoundException.class, () -> service.deleteById("nonexistent-id"));
    }

    /**
     * Simple in-memory fake used only for testing the service in isolation,
     * without depending on JsonFileExpenseStore or touching the filesystem.
     */
    static class FakeExpenseStore implements ExpenseStore {
        private final List<Expense> expenses = new ArrayList<>();

        @Override
        public Expense save(Expense expense) {
            expenses.add(expense);
            return expense;
        }

        @Override
        public List<Expense> findAll() {
            return new ArrayList<>(expenses);
        }

        @Override
        public Optional<Expense> findById(String id) {
            return expenses.stream().filter(e -> e.id().equals(id)).findFirst();
        }

        @Override
        public boolean deleteById(String id) {
            return expenses.removeIf(e -> e.id().equals(id));
        }
    }
}