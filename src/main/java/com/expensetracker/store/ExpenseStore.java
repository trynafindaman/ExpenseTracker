package com.expensetracker.store;

import com.expensetracker.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseStore {

    Expense save(Expense expense);

    List<Expense> findAll();

    Optional<Expense> findById(String id);

    boolean deleteById(String id);
}