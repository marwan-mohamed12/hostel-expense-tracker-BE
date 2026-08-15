package com.hostel.tracker.payment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthRecordRepository extends JpaRepository<MonthRecord, String> {

    List<MonthRecord> findAllByOrderByIdDesc();

    List<MonthRecord> findByIdGreaterThanEqual(String monthId);
}
