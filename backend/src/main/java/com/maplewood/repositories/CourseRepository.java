package com.maplewood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

public interface CourseRepository extends JpaRepository<Course, Integer> {

    @EntityGraph(attributePaths = { "specialization", "prerequisite" })
    @Query("SELECT c FROM Course c")
    Page<Course> findAllWithSpecializationAndPrerequisite(@NonNull Pageable pageable);

    Page<Course> findAllBySemesterOrder(SemesterOrder order, @NonNull Pageable pageable);
}
