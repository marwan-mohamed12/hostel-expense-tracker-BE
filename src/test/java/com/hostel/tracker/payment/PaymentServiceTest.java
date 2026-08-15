package com.hostel.tracker.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.hostel.tracker.common.MonthIds;
import com.hostel.tracker.resident.Resident;
import com.hostel.tracker.resident.ResidentDtos;
import com.hostel.tracker.resident.ResidentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentServiceTest {

    @Autowired
    private ResidentService residents;

    @Autowired
    private PaymentService payments;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void openingMonthSeedsUnpaidRowsForActiveResidentsOnly() {
        residents.create(new ResidentDtos.ResidentInput("Active", "", "1", new BigDecimal("250"), true, ""));
        residents.create(new ResidentDtos.ResidentInput("Inactive", "", "2", new BigDecimal("250"), false, ""));

        payments.createMonth(new PaymentDtos.MonthCreateRequest("2030-01", null, null));

        var rows = payments.listPaymentsForMonth("2030-01");
        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().paid()).isFalse();
        assertThat(rows.getFirst().amount()).isEqualTo(250.0);
    }

    @Test
    void feeChangeUpdatesUnpaidAmountsOnly() {
        var resident = residents.create(new ResidentDtos.ResidentInput("Sam", "", "3", new BigDecimal("250"), true, ""));
        payments.createMonth(new PaymentDtos.MonthCreateRequest("2030-02", null, null));
        var payment = payments.listPaymentsForMonth("2030-02").getFirst();
        payments.updatePayment(payment.id(), new PaymentDtos.PaymentUpdateRequest(null, true, "2030-02-10", null));

        residents.update(resident.id(), new ResidentDtos.ResidentInput("Sam", "", "3", new BigDecimal("300"), true, ""));

        var paidRow = paymentRepository.findById(payment.id()).orElseThrow();
        assertThat(paidRow.isPaid()).isTrue();
        assertThat(paidRow.getAmount()).isEqualByComparingTo("250");

        payments.updatePayment(payment.id(), new PaymentDtos.PaymentUpdateRequest(null, false, null, null));
        residents.update(resident.id(), new ResidentDtos.ResidentInput("Sam", "", "3", new BigDecimal("400"), true, ""));
        var unpaid = paymentRepository.findById(payment.id()).orElseThrow();
        assertThat(unpaid.getAmount()).isEqualByComparingTo("400");
    }

    @Test
    void getDoesNotCreateCurrentMonth() {
        assertThat(payments.listMonths().stream().anyMatch(m -> m.id().equals(MonthIds.current()))).isFalse();
    }
}
