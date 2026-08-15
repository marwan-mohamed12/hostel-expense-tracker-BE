package com.hostel.tracker.expense;

import com.hostel.tracker.common.ApiException;
import com.hostel.tracker.common.ExpenseCategories;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenses;
    private final CustomCategoryRepository categories;

    public ExpenseService(ExpenseRepository expenses, CustomCategoryRepository categories) {
        this.expenses = expenses;
        this.categories = categories;
    }

    @Transactional(readOnly = true)
    public List<ExpenseDtos.ExpenseResponse> list() {
        return expenses.findAllByOrderByDateDescCreatedAtDesc().stream()
                .map(ExpenseDtos.ExpenseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> listCustomCategories() {
        return categories.findAllByOrderByNameAsc().stream().map(CustomCategory::getName).toList();
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse create(ExpenseDtos.ExpenseInput input) {
        Instant now = Instant.now();
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        apply(expense, input);
        expense.setCreatedAt(now);
        expense.setUpdatedAt(now);
        return ExpenseDtos.ExpenseResponse.from(expenses.save(expense));
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse update(String id, ExpenseDtos.ExpenseInput input) {
        Expense expense = require(id);
        apply(expense, input);
        expense.setUpdatedAt(Instant.now());
        return ExpenseDtos.ExpenseResponse.from(expenses.save(expense));
    }

    @Transactional
    public ExpenseDtos.ExpenseResponse setPaid(String id, boolean paid) {
        Expense expense = require(id);
        expense.setPaid(paid);
        expense.setUpdatedAt(Instant.now());
        return ExpenseDtos.ExpenseResponse.from(expenses.save(expense));
    }

    @Transactional
    public void delete(String id) {
        require(id);
        expenses.deleteById(id);
    }

    @Transactional
    public String addCategory(String name) {
        String normalized = ExpenseCategories.normalize(name);
        if (!ExpenseCategories.isBuiltin(normalized)) {
            boolean exists = categories.findAll().stream()
                    .anyMatch(item -> item.getName().equalsIgnoreCase(normalized));
            if (!exists) {
                categories.save(new CustomCategory(normalized));
            }
        }
        return normalized;
    }

    private Expense require(String id) {
        return expenses.findById(id).orElseThrow(() -> ApiException.notFound("Expense not found"));
    }

    private void apply(Expense expense, ExpenseDtos.ExpenseInput input) {
        String category = addCategory(input.category());
        expense.setTitle(safe(input.title()));
        expense.setCategory(category);
        expense.setAmount(input.amount() == null ? BigDecimal.ZERO : input.amount());
        expense.setDate(input.date() == null || input.date().isBlank() ? LocalDate.now() : LocalDate.parse(input.date()));
        expense.setDescription(safe(input.description()));
        expense.setAddedBy(safe(input.addedBy()));
        expense.setPaid(input.paid() == null || input.paid());
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
