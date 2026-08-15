package com.hostel.tracker.payment;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final PaymentService payments;

    public PaymentController(PaymentService payments) {
        this.payments = payments;
    }

    @GetMapping("/api/months")
    public List<PaymentDtos.MonthResponse> listMonths() {
        return payments.listMonths();
    }

    @PostMapping("/api/months")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDtos.MonthResponse createMonth(@RequestBody(required = false) PaymentDtos.MonthCreateRequest request) {
        return payments.createMonth(request == null ? new PaymentDtos.MonthCreateRequest(null, null, null) : request);
    }

    @PostMapping("/api/months/ensure-current")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDtos.MonthResponse ensureCurrent() {
        return payments.ensureCurrentMonth();
    }

    @DeleteMapping("/api/months/{monthId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDtos.MonthResponse> deleteMonth(@PathVariable String monthId) {
        PaymentDtos.MonthResponse recreated = payments.removeMonth(monthId);
        if (recreated == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recreated);
    }

    @GetMapping("/api/months/{monthId}/payments")
    public List<PaymentDtos.PaymentResponse> listMonthPayments(@PathVariable String monthId) {
        return payments.listPaymentsForMonth(monthId);
    }

    @PatchMapping("/api/payments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentDtos.PaymentResponse updatePayment(
            @PathVariable String id,
            @RequestBody PaymentDtos.PaymentUpdateRequest request
    ) {
        return payments.updatePayment(id, request);
    }
}
