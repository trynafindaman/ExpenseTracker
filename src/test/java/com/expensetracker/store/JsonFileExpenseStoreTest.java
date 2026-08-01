package com.expensetracker.store;

import com.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileExpenseStoreTest {

    private JsonFileExpenseStore store;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        Path tempFile = tempDir.resolve("expenses.json");
        store = new JsonFileExpenseStore(tempFile);
    }

    @Test
    void findAll_returnsEmptyList_whenFileDoesNotExistYet() {
        List<Expense> result = store.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void save_thenFindAll_returnsSavedExpense() {
        Expense expense = sampleExpense();

        store.save(expense);
        List<Expense> all = store.findAll();

        assertEquals(1, all.size());
        assertEquals(expense, all.get(0));
    }

    @Test
    void save_thenFindById_returnsMatchingExpense() {
        Expense expense = sampleExpense();
        store.save(expense);

        Optional<Expense> found = store.findById(expense.id());

        assertTrue(found.isPresent());
        assertEquals(expense, found.get());
    }

    @Test
    void findById_returnsEmpty_whenIdDoesNotExist() {
        Optional<Expense> found = store.findById(UUID.randomUUID().toString());
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_removesExpense_andReturnsTrue() {
        Expense expense = sampleExpense();
        store.save(expense);

        boolean deleted = store.deleteById(expense.id());

        assertTrue(deleted);
        assertTrue(store.findAll().isEmpty());
    }

    @Test
    void deleteById_returnsFalse_whenIdDoesNotExist() {
        boolean deleted = store.deleteById(UUID.randomUUID().toString());
        assertFalse(deleted);
    }

    @Test
    void dataPersists_acrossNewStoreInstance_pointingAtSameFile() {
        Expense expense = sampleExpense();
        store.save(expense);

        // Simulate an "app restart" by creating a fresh store instance
        // pointed at the same underlying file.
        JsonFileExpenseStore reloadedStore =
                new JsonFileExpenseStore(tempDir.resolve("expenses.json"));

        List<Expense> all = reloadedStore.findAll();
        assertEquals(1, all.size());
        assertEquals(expense, all.get(0));
    }

    private Expense sampleExpense() {
        return new Expense(
                UUID.randomUUID().toString(),
                "Groceries",
                new BigDecimal("45.30"),
                "Food",
                LocalDate.of(2026, 6, 5)
        );
    }
}