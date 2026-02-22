package com.maplewood.repositories;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.maplewood.domain.Specialization;

public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {

    @Query("""
            SELECT c.id AS courseId, s AS specialization
            FROM Course c
            JOIN c.specialization s
            WHERE c.id IN :courseIds
            """)
    List<CourseSpecializationProjection> findSpecializationsByCourseIds(
            Collection<Integer> courseIds);

    interface CourseSpecializationProjection {
        Integer getCourseId();

        Specialization getSpecialization();
    }
}
