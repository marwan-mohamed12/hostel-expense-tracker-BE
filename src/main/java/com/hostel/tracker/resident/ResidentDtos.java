package com.hostel.tracker.resident;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public final class ResidentDtos {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_INSTANT;

    private ResidentDtos() {
    }

    public record ResidentInput(
            @NotBlank String name,
            String phone,
            String room,
            @NotNull @PositiveOrZero BigDecimal monthlyFee,
            Boolean active,
            String notes
    ) {
    }

    public record ActivePatch(@NotNull Boolean active) {
    }

    public record ResidentResponse(
            String id,
            String name,
            String phone,
            String room,
            double monthlyFee,
            boolean active,
            String notes,
            String createdAt,
            String updatedAt
    ) {
        public static ResidentResponse from(Resident resident) {
            return new ResidentResponse(
                    resident.getId(),
                    resident.getName(),
                    resident.getPhone(),
                    resident.getRoom(),
                    resident.getMonthlyFee().doubleValue(),
                    resident.isActive(),
                    resident.getNotes(),
                    ISO.format(resident.getCreatedAt()),
                    ISO.format(resident.getUpdatedAt())
            );
        }
    }
}
