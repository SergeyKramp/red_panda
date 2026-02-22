package com.maplewood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maplewood.domain.StudentEnrollment;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Integer> {
}
