package com.hostel.tracker.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public final class ExpenseDtos {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private ExpenseDtos() {
    }

    public record ExpenseInput(
            @NotBlank String title,
            String category,
            @NotNull @PositiveOrZero BigDecimal amount,
            String date,
            String description,
            String addedBy,
            Boolean paid
    ) {
    }

    public record PaidPatch(@NotNull Boolean paid) {
    }

    public record CategoryCreateRequest(@NotBlank String name) {
    }

    public record CategoryResponse(String name) {
    }

    public record ExpenseResponse(
            String id,
            String title,
            String category,
            double amount,
            String date,
            String description,
            String addedBy,
            boolean paid,
            String createdAt,
            String updatedAt
    ) {
        public static ExpenseResponse from(Expense expense) {
            return new ExpenseResponse(
                    expense.getId(),
                    expense.getTitle(),
                    expense.getCategory(),
                    expense.getAmount().doubleValue(),
                    expense.getDate().toString(),
                    expense.getDescription(),
                    expense.getAddedBy(),
                    expense.isPaid(),
                    ISO.format(expense.getCreatedAt()),
                    ISO.format(expense.getUpdatedAt())
            );
        }
    }
}
