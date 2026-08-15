package com.hostel.tracker.payment;

import com.hostel.tracker.common.ApiException;
import com.hostel.tracker.common.MonthIds;
import com.hostel.tracker.resident.Resident;
import com.hostel.tracker.resident.ResidentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private static final BigDecimal DEFAULT_FEE = new BigDecimal("250");

    private final MonthRecordRepository months;
    private final PaymentRepository payments;
    private final ResidentRepository residents;

    public PaymentService(
            MonthRecordRepository months,
            PaymentRepository payments,
            ResidentRepository residents
    ) {
        this.months = months;
        this.payments = payments;
        this.residents = residents;
    }

    @Transactional(readOnly = true)
    public List<PaymentDtos.MonthResponse> listMonths() {
        return months.findAllByOrderByIdDesc().stream().map(PaymentDtos.MonthResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDtos.PaymentResponse> listAllPayments() {
        return payments.findAll().stream().map(PaymentDtos.PaymentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentDtos.PaymentResponse> listPaymentsForMonth(String monthId) {
        MonthIds.parse(monthId);
        return payments.findByMonthId(monthId).stream().map(PaymentDtos.PaymentResponse::from).toList();
    }

    @Transactional
    public PaymentDtos.MonthResponse createMonth(PaymentDtos.MonthCreateRequest request) {
        String monthId = resolveMonthId(request);
        return PaymentDtos.MonthResponse.from(openMonth(monthId));
    }

    @Transactional
    public PaymentDtos.MonthResponse ensureCurrentMonth() {
        return PaymentDtos.MonthResponse.from(openMonth(MonthIds.current()));
    }

    @Transactional
    public PaymentDtos.MonthResponse removeMonth(String monthId) {
        MonthIds.parse(monthId);
        if (!months.existsById(monthId)) {
            throw ApiException.notFound("Month not found");
        }
        payments.deleteByMonthId(monthId);
        months.deleteById(monthId);

        String currentId = MonthIds.current();
        if (monthId.equals(currentId)) {
            return PaymentDtos.MonthResponse.from(openMonth(currentId));
        }
        return null;
    }

    @Transactional
    public PaymentDtos.PaymentResponse updatePayment(String id, PaymentDtos.PaymentUpdateRequest request) {
        Payment payment = payments.findById(id).orElseThrow(() -> ApiException.notFound("Payment not found"));
        if (request.amount() != null) {
            payment.setAmount(request.amount());
        }
        if (request.notes() != null) {
            payment.setNotes(request.notes());
        }
        if (request.paid() != null) {
            payment.setPaid(request.paid());
        }
        if (request.paidAt() != null) {
            payment.setPaidAt(request.paidAt().isBlank() ? null : LocalDate.parse(request.paidAt()));
        }
        if (payment.isPaid() && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDate.now());
        }
        if (!payment.isPaid()) {
            payment.setPaidAt(null);
        }
        payment.setUpdatedAt(Instant.now());
        return PaymentDtos.PaymentResponse.from(payments.save(payment));
    }

    @Transactional
    public void seedForNewResident(Resident resident) {
        if (!resident.isActive()) {
            return;
        }
        String currentId = MonthIds.current();
        List<MonthRecord> targets = new ArrayList<>(months.findByIdGreaterThanEqual(currentId));
        if (targets.stream().noneMatch(month -> month.getId().equals(currentId))) {
            openMonth(currentId);
            return;
        }
        Instant now = Instant.now();
        for (MonthRecord month : targets) {
            if (payments.findByMonthIdAndResidentId(month.getId(), resident.getId()).isPresent()) {
                continue;
            }
            payments.save(unpaidRow(month.getId(), resident, now));
        }
    }

    @Transactional
    public void syncUnpaidAmounts(Resident resident) {
        List<Payment> rows = payments.findByResidentId(resident.getId());
        Instant now = Instant.now();
        for (Payment payment : rows) {
            if (!payment.isPaid()) {
                payment.setAmount(resident.getMonthlyFee());
                payment.setUpdatedAt(now);
            }
        }
        payments.saveAll(rows);
    }

    @Transactional
    public void deleteForResident(String residentId) {
        payments.deleteByResidentId(residentId);
    }

    private MonthRecord openMonth(String monthId) {
        YearMonth parsed = MonthIds.parse(monthId);
        MonthRecord record = months.findById(monthId).orElseGet(() -> {
            MonthRecord created = new MonthRecord();
            created.setId(monthId);
            created.setYear(parsed.getYear());
            created.setMonth(parsed.getMonthValue());
            created.setLabel(MonthIds.label(parsed));
            created.setCreatedAt(Instant.now());
            return months.save(created);
        });
        seedMissingPayments(monthId);
        return record;
    }

    private void seedMissingPayments(String monthId) {
        Set<String> existing = payments.findByMonthId(monthId).stream()
                .map(Payment::getResidentId)
                .collect(Collectors.toSet());
        Instant now = Instant.now();
        List<Payment> missing = residents.findByActiveTrue().stream()
                .filter(resident -> !existing.contains(resident.getId()))
                .map(resident -> unpaidRow(monthId, resident, now))
                .toList();
        if (!missing.isEmpty()) {
            payments.saveAll(missing);
        }
    }

    private Payment unpaidRow(String monthId, Resident resident, Instant now) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setMonthId(monthId);
        payment.setResidentId(resident.getId());
        payment.setAmount(resident.getMonthlyFee() == null ? DEFAULT_FEE : resident.getMonthlyFee());
        payment.setPaid(false);
        payment.setPaidAt(null);
        payment.setNotes("");
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        return payment;
    }

    private String resolveMonthId(PaymentDtos.MonthCreateRequest request) {
        if (request != null && request.monthId() != null && !request.monthId().isBlank()) {
            return request.monthId();
        }
        if (request != null && request.year() != null && request.month() != null) {
            return MonthIds.of(request.year(), request.month());
        }
        throw ApiException.badRequest("Provide monthId or year+month");
    }
}
