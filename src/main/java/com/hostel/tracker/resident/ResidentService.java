package com.hostel.tracker.resident;

import com.hostel.tracker.common.ApiException;
import com.hostel.tracker.payment.PaymentService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResidentService {

    private static final BigDecimal DEFAULT_FEE = new BigDecimal("250");

    private final ResidentRepository residents;
    private final PaymentService paymentService;

    public ResidentService(ResidentRepository residents, PaymentService paymentService) {
        this.residents = residents;
        this.paymentService = paymentService;
    }

    @Transactional(readOnly = true)
    public List<ResidentDtos.ResidentResponse> list() {
        return residents.findAllByOrderByNameAsc().stream().map(ResidentDtos.ResidentResponse::from).toList();
    }

    @Transactional
    public ResidentDtos.ResidentResponse create(ResidentDtos.ResidentInput input) {
        Instant now = Instant.now();
        Resident resident = new Resident();
        resident.setId(UUID.randomUUID().toString());
        apply(resident, input);
        resident.setCreatedAt(now);
        resident.setUpdatedAt(now);
        Resident saved = residents.save(resident);
        paymentService.seedForNewResident(saved);
        return ResidentDtos.ResidentResponse.from(saved);
    }

    @Transactional
    public ResidentDtos.ResidentResponse update(String id, ResidentDtos.ResidentInput input) {
        Resident resident = require(id);
        apply(resident, input);
        resident.setUpdatedAt(Instant.now());
        Resident saved = residents.save(resident);
        paymentService.syncUnpaidAmounts(saved);
        paymentService.seedForNewResident(saved);
        return ResidentDtos.ResidentResponse.from(saved);
    }

    @Transactional
    public ResidentDtos.ResidentResponse setActive(String id, boolean active) {
        Resident resident = require(id);
        ResidentDtos.ResidentInput input = new ResidentDtos.ResidentInput(
                resident.getName(),
                resident.getPhone(),
                resident.getRoom(),
                resident.getMonthlyFee(),
                active,
                resident.getNotes()
        );
        return update(id, input);
    }

    @Transactional
    public void delete(String id) {
        require(id);
        paymentService.deleteForResident(id);
        residents.deleteById(id);
    }

    private Resident require(String id) {
        return residents.findById(id).orElseThrow(() -> ApiException.notFound("Resident not found"));
    }

    private void apply(Resident resident, ResidentDtos.ResidentInput input) {
        resident.setName(safe(input.name()));
        resident.setPhone(safe(input.phone()));
        resident.setRoom(safe(input.room()));
        BigDecimal fee = input.monthlyFee() == null || input.monthlyFee().signum() == 0
                ? DEFAULT_FEE
                : input.monthlyFee();
        resident.setMonthlyFee(fee);
        resident.setActive(input.active() == null || input.active());
        resident.setNotes(safe(input.notes()));
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
