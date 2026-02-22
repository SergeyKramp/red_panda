package com.maplewood.repositories;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import com.maplewood.domain.StudentCourseHistory;

public interface StudentCourseHistoryRepository
        extends JpaRepository<StudentCourseHistory, Integer> {
    @Query("""
            SELECT DISTINCT sch.course.id
            FROM StudentCourseHistory sch
            WHERE sch.student.id = :studentId AND sch.status = 'passed'
             """)
    Set<Integer> findPassedCourseIdsByStudentId(@NonNull Integer studentId);

    @Query("""
            SELECT COUNT(sch.id)
            FROM StudentCourseHistory sch
            WHERE sch.student.id = :studentId AND sch.semester.active = true
            """)
    long countActiveSemesterCoursesByStudentId(@NonNull Integer studentId);
}
