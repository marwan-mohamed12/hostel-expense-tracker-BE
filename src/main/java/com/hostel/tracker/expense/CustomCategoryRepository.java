package com.hostel.tracker.expense;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomCategoryRepository extends JpaRepository<CustomCategory, String> {

    List<CustomCategory> findAllByOrderByNameAsc();
}
