package com.hostel.tracker.expense;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExpenseController {

    private final ExpenseService expenses;

    public ExpenseController(ExpenseService expenses) {
        this.expenses = expenses;
    }

    @GetMapping("/api/expenses")
    public List<ExpenseDtos.ExpenseResponse> list() {
        return expenses.list();
    }

    @PostMapping("/api/expenses")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpenseDtos.ExpenseResponse create(@Valid @RequestBody ExpenseDtos.ExpenseInput input) {
        return expenses.create(input);
    }

    @PatchMapping("/api/expenses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpenseDtos.ExpenseResponse update(
            @PathVariable String id,
            @Valid @RequestBody ExpenseDtos.ExpenseInput input
    ) {
        return expenses.update(id, input);
    }

    @PatchMapping("/api/expenses/{id}/paid")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpenseDtos.ExpenseResponse setPaid(
            @PathVariable String id,
            @Valid @RequestBody ExpenseDtos.PaidPatch patch
    ) {
        return expenses.setPaid(id, patch.paid());
    }

    @DeleteMapping("/api/expenses/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        expenses.delete(id);
    }

    @GetMapping("/api/categories")
    public List<String> categories() {
        return expenses.listCustomCategories();
    }

    @PostMapping("/api/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ExpenseDtos.CategoryResponse addCategory(@Valid @RequestBody ExpenseDtos.CategoryCreateRequest request) {
        return new ExpenseDtos.CategoryResponse(expenses.addCategory(request.name()));
    }
}
