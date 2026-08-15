package com.hostel.tracker.resident;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<Resident, String> {

    List<Resident> findAllByOrderByNameAsc();

    List<Resident> findByActiveTrue();
}
