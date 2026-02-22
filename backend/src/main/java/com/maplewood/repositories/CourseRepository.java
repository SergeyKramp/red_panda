package com.maplewood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    Page<Course> findAllBySemesterOrder(SemesterOrder order, Pageable pageable);

    default Page<Course> findAllByPage(Pageable pageable) {
        return findAll(pageable);
    }
}
