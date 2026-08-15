package com.hostel.tracker.expense;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "custom_categories")
public class CustomCategory {

    @Id
    @Column(length = 120)
    private String name;

    public CustomCategory() {
    }

    public CustomCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
