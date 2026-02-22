package com.maplewood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maplewood.domain.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}
