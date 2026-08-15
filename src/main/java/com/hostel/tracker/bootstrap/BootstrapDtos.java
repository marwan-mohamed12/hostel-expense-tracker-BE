package com.hostel.tracker.bootstrap;

import com.hostel.tracker.expense.ExpenseDtos;
import com.hostel.tracker.payment.PaymentDtos;
import com.hostel.tracker.resident.ResidentDtos;
import java.util.List;

public final class BootstrapDtos {

    private BootstrapDtos() {
    }

    public record BootstrapResponse(
            List<ResidentDtos.ResidentResponse> residents,
            List<PaymentDtos.MonthResponse> months,
            List<PaymentDtos.PaymentResponse> payments,
            List<ExpenseDtos.ExpenseResponse> expenses,
            List<String> customCategories
    ) {
    }
}
