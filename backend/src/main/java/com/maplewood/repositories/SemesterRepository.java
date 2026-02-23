package com.maplewood.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maplewood.domain.Semester;

public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    Optional<Semester> findFirstByActiveTrueOrderByYearDescOrderInYearDesc();
}
