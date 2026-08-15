package com.hostel.tracker.payment;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public final class PaymentDtos {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private PaymentDtos() {
    }

    public record MonthCreateRequest(String monthId, Integer year, Integer month) {
    }

    public record PaymentUpdateRequest(
            BigDecimal amount,
            Boolean paid,
            String paidAt,
            String notes
    ) {
    }

    public record MonthResponse(
            String id,
            int year,
            int month,
            String label,
            String createdAt
    ) {
        public static MonthResponse from(MonthRecord record) {
            return new MonthResponse(
                    record.getId(),
                    record.getYear(),
                    record.getMonth(),
                    record.getLabel(),
                    ISO.format(record.getCreatedAt())
            );
        }
    }

    public record PaymentResponse(
            String id,
            String monthId,
            String residentId,
            double amount,
            boolean paid,
            String paidAt,
            String notes,
            String createdAt,
            String updatedAt
    ) {
        public static PaymentResponse from(Payment payment) {
            return new PaymentResponse(
                    payment.getId(),
                    payment.getMonthId(),
                    payment.getResidentId(),
                    payment.getAmount().doubleValue(),
                    payment.isPaid(),
                    payment.getPaidAt() == null ? null : payment.getPaidAt().toString(),
                    payment.getNotes(),
                    ISO.format(payment.getCreatedAt()),
                    ISO.format(payment.getUpdatedAt())
            );
        }
    }
}
