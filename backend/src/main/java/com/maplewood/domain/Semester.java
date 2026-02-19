package com.maplewood.domain;

import java.time.Instant;
import com.maplewood.persistence.converter.InstantStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "semesters", uniqueConstraints = {
        @UniqueConstraint(name = "uk_semesters_name_year", columnNames = {"name", "year"})})
public class Semester {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "order_in_year", nullable = false)
    @Convert(converter = SemesterOrder.SemesterOrderConverter.class)
    private SemesterOrder orderInYear;

    @Column(name = "start_date")
    @Convert(converter = InstantStringConverter.class)
    private Instant startDate;

    @Column(name = "end_date")
    @Convert(converter = InstantStringConverter.class)
    private Instant endDate;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "created_at")
    @Convert(converter = InstantStringConverter.class)
    private Instant createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public SemesterOrder getOrderInYear() {
        return orderInYear;
    }

    public void setOrderInYear(SemesterOrder orderInYear) {
        this.orderInYear = orderInYear;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
