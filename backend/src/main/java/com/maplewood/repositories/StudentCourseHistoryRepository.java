package com.maplewood.repositories;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseHistoryStatus;
import com.maplewood.domain.StudentCourseHistory;

public interface StudentCourseHistoryRepository
        extends JpaRepository<StudentCourseHistory, Integer> {
    interface CourseWithStatusProjection {
        Course getCourse();

        CourseHistoryStatus getStatus();
    }

    @Query("""
            SELECT DISTINCT sch.course.id
            FROM StudentCourseHistory sch
            WHERE sch.student.id = :studentId AND sch.status = 'passed'
             """)
    Set<Integer> findPassedCourseIdsByStudentId(@NonNull Integer studentId);

    @Query("""
            SELECT COALESCE(SUM(c.credits), 0)
            FROM Course c
            WHERE c.id IN (
                SELECT DISTINCT sch.course.id
                FROM StudentCourseHistory sch
                WHERE sch.student.id = ?1
                        AND sch.status = com.maplewood.domain.CourseHistoryStatus.PASSED
            )
            """)
    Double findEarnedCreditsByStudentId(@NonNull Integer studentId);

    @Query("""
            SELECT c as course,
            CASE
                WHEN SUM(
                    CASE
                        WHEN sch.status = com.maplewood.domain.CourseHistoryStatus.PASSED
                            THEN 1
                        ELSE 0
                    END
                ) > 0
                    THEN com.maplewood.domain.CourseHistoryStatus.PASSED
                ELSE com.maplewood.domain.CourseHistoryStatus.FAILED
            END as status
            FROM StudentCourseHistory sch
            JOIN sch.course c
            WHERE sch.student.id = ?1
            GROUP BY c
            """)
    List<CourseWithStatusProjection> findCoursesByStudentId(@NonNull Integer studentId);

    List<StudentCourseHistory> findByStudentId(Integer studentId);
}
