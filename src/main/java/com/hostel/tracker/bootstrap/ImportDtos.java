package com.hostel.tracker.bootstrap;

import java.math.BigDecimal;
import java.util.List;

public final class ImportDtos {

    private ImportDtos() {
    }

    public record AppDataImport(
            List<ResidentRow> residents,
            List<MonthRow> months,
            List<PaymentRow> payments,
            List<ExpenseRow> expenses,
            List<String> customCategories
    ) {
    }

    public record ResidentRow(
            String id,
            String name,
            String phone,
            String room,
            BigDecimal monthlyFee,
            Boolean active,
            String notes,
            String createdAt,
            String updatedAt
    ) {
    }

    public record MonthRow(
            String id,
            Integer year,
            Integer month,
            String label,
            String createdAt
    ) {
    }

    public record PaymentRow(
            String id,
            String monthId,
            String residentId,
            BigDecimal amount,
            Boolean paid,
            String paidAt,
            String notes,
            String createdAt,
            String updatedAt
    ) {
    }

    public record ExpenseRow(
            String id,
            String title,
            String category,
            BigDecimal amount,
            String date,
            String description,
            String addedBy,
            Boolean paid,
            String createdAt,
            String updatedAt
    ) {
    }

    public record ImportResult(int residents, int months, int payments, int expenses, int customCategories) {
    }
}
