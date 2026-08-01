package com.expensetracker.store;

import com.expensetracker.model.Expense;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JsonFileExpenseStore implements ExpenseStore {

    private final Path filePath;
    private final ObjectMapper objectMapper;

    public JsonFileExpenseStore() {
        this(Path.of("expenses.json"));
    }

    // Package-private constructor used by tests to point at a temp file
    // instead of the real data file, so tests never touch production data.
    JsonFileExpenseStore(Path filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    public synchronized Expense save(Expense expense) {
        List<Expense> all = readAll();
        all.add(expense);
        writeAll(all);
        return expense;
    }

    @Override
    public synchronized List<Expense> findAll() {
        return readAll();
    }

    @Override
    public synchronized Optional<Expense> findById(String id) {
        return readAll().stream()
                .filter(e -> e.id().equals(id))
                .findFirst();
    }

    @Override
    public synchronized boolean deleteById(String id) {
        List<Expense> all = readAll();
        boolean removed = all.removeIf(e -> e.id().equals(id));
        if (removed) {
            writeAll(all);
        }
        return removed;
    }

    private List<Expense> readAll() {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }
        try {
            Expense[] expenses = objectMapper.readValue(filePath.toFile(), Expense[].class);
            return new ArrayList<>(List.of(expenses));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read expenses from " + filePath, e);
        }
    }

    private void writeAll(List<Expense> expenses) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), expenses);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write expenses to " + filePath, e);
        }
    }
}