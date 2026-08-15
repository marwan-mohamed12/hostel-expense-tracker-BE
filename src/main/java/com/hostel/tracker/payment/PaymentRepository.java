package com.hostel.tracker.payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByMonthId(String monthId);

    List<Payment> findByResidentId(String residentId);

    Optional<Payment> findByMonthIdAndResidentId(String monthId, String residentId);

    void deleteByResidentId(String residentId);

    void deleteByMonthId(String monthId);
}
