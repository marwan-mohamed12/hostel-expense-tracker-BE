package com.hostel.tracker.bootstrap;

import com.hostel.tracker.common.ExpenseCategories;
import com.hostel.tracker.common.MonthIds;
import com.hostel.tracker.expense.CustomCategory;
import com.hostel.tracker.expense.CustomCategoryRepository;
import com.hostel.tracker.expense.Expense;
import com.hostel.tracker.expense.ExpenseRepository;
import com.hostel.tracker.payment.MonthRecord;
import com.hostel.tracker.payment.MonthRecordRepository;
import com.hostel.tracker.payment.Payment;
import com.hostel.tracker.payment.PaymentRepository;
import com.hostel.tracker.resident.Resident;
import com.hostel.tracker.resident.ResidentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportService {

    private final ResidentRepository residents;
    private final MonthRecordRepository months;
    private final PaymentRepository payments;
    private final ExpenseRepository expenses;
    private final CustomCategoryRepository categories;

    public ImportService(
            ResidentRepository residents,
            MonthRecordRepository months,
            PaymentRepository payments,
            ExpenseRepository expenses,
            CustomCategoryRepository categories
    ) {
        this.residents = residents;
        this.months = months;
        this.payments = payments;
        this.expenses = expenses;
        this.categories = categories;
    }

    @Transactional
    public ImportDtos.ImportResult importData(ImportDtos.AppDataImport data) {
        payments.deleteAll();
        expenses.deleteAll();
        months.deleteAll();
        residents.deleteAll();
        categories.deleteAll();

        List<ImportDtos.ResidentRow> residentRows = orEmpty(data.residents());
        for (ImportDtos.ResidentRow row : residentRows) {
            Resident resident = new Resident();
            resident.setId(orId(row.id()));
            resident.setName(safe(row.name()));
            resident.setPhone(safe(row.phone()));
            resident.setRoom(safe(row.room()));
            resident.setMonthlyFee(row.monthlyFee() == null ? new BigDecimal("250") : row.monthlyFee());
            resident.setActive(row.active() == null || row.active());
            resident.setNotes(safe(row.notes()));
            resident.setCreatedAt(parseInstant(row.createdAt()));
            resident.setUpdatedAt(parseInstant(row.updatedAt()));
            residents.save(resident);
        }

        List<ImportDtos.MonthRow> monthRows = orEmpty(data.months());
        for (ImportDtos.MonthRow row : monthRows) {
            YearMonth parsed = MonthIds.parse(row.id());
            MonthRecord month = new MonthRecord();
            month.setId(row.id());
            month.setYear(row.year() == null ? parsed.getYear() : row.year());
            month.setMonth(row.month() == null ? parsed.getMonthValue() : row.month());
            month.setLabel(row.label() == null || row.label().isBlank() ? MonthIds.label(parsed) : row.label());
            month.setCreatedAt(parseInstant(row.createdAt()));
            months.save(month);
        }

        List<ImportDtos.PaymentRow> paymentRows = orEmpty(data.payments());
        for (ImportDtos.PaymentRow row : paymentRows) {
            Payment payment = new Payment();
            payment.setId(orId(row.id()));
            payment.setMonthId(row.monthId());
            payment.setResidentId(row.residentId());
            payment.setAmount(row.amount() == null ? BigDecimal.ZERO : row.amount());
            payment.setPaid(Boolean.TRUE.equals(row.paid()));
            payment.setPaidAt(parseDateOrNull(row.paidAt()));
            payment.setNotes(safe(row.notes()));
            payment.setCreatedAt(parseInstant(row.createdAt()));
            payment.setUpdatedAt(parseInstant(row.updatedAt()));
            if (payment.isPaid() && payment.getPaidAt() == null) {
                payment.setPaidAt(LocalDate.now());
            }
            if (!payment.isPaid()) {
                payment.setPaidAt(null);
            }
            payments.save(payment);
        }

        List<ImportDtos.ExpenseRow> expenseRows = orEmpty(data.expenses());
        for (ImportDtos.ExpenseRow row : expenseRows) {
            Expense expense = new Expense();
            expense.setId(orId(row.id()));
            expense.setTitle(safe(row.title()));
            expense.setCategory(ExpenseCategories.normalize(row.category()));
            expense.setAmount(row.amount() == null ? BigDecimal.ZERO : row.amount());
            expense.setDate(parseDateOrNull(row.date()) == null ? LocalDate.now() : parseDateOrNull(row.date()));
            expense.setDescription(safe(row.description()));
            expense.setAddedBy(safe(row.addedBy()));
            expense.setPaid(row.paid() == null || row.paid());
            expense.setCreatedAt(parseInstant(row.createdAt()));
            expense.setUpdatedAt(parseInstant(row.updatedAt()));
            expenses.save(expense);
        }

        List<String> custom = orEmpty(data.customCategories());
        int savedCategories = 0;
        for (String name : custom) {
            String normalized = ExpenseCategories.normalize(name);
            if (!ExpenseCategories.isBuiltin(normalized) && !categories.existsById(normalized)) {
                categories.save(new CustomCategory(normalized));
                savedCategories++;
            }
        }

        return new ImportDtos.ImportResult(
                residentRows.size(),
                monthRows.size(),
                paymentRows.size(),
                expenseRows.size(),
                savedCategories
        );
    }

    private static <T> List<T> orEmpty(List<T> list) {
        return list == null ? List.of() : list;
    }

    private static String orId(String id) {
        return id == null || id.isBlank() ? UUID.randomUUID().toString() : id;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static Instant parseInstant(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        try {
            return Instant.parse(value);
        } catch (Exception ex) {
            return Instant.now();
        }
    }

    private static LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.length() >= 10 ? value.substring(0, 10) : value);
        } catch (Exception ex) {
            return null;
        }
    }
}
