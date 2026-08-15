package com.hostel.tracker.bootstrap;

import com.hostel.tracker.expense.ExpenseService;
import com.hostel.tracker.payment.PaymentService;
import com.hostel.tracker.resident.ResidentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootstrapService {

    private final ResidentService residents;
    private final PaymentService payments;
    private final ExpenseService expenses;

    public BootstrapService(ResidentService residents, PaymentService payments, ExpenseService expenses) {
        this.residents = residents;
        this.payments = payments;
        this.expenses = expenses;
    }

    @Transactional(readOnly = true)
    public BootstrapDtos.BootstrapResponse load() {
        return new BootstrapDtos.BootstrapResponse(
                residents.list(),
                payments.listMonths(),
                payments.listAllPayments(),
                expenses.list(),
                expenses.listCustomCategories()
        );
    }
}
