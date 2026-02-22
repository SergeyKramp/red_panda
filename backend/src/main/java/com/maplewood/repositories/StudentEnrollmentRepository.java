package com.maplewood.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import com.maplewood.domain.StudentEnrollment;
import com.maplewood.domain.StudentEnrollmentStatus;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Integer> {
        boolean existsByStudentIdAndCourseIdAndStatus(Integer studentId, Integer courseId,
                        StudentEnrollmentStatus status);

        @Modifying
        @Transactional
        @Query(value = """
                        INSERT INTO student_enrollments (student_id, course_id, semester_id, status, created_at)
                        SELECT :studentId, :courseId, s.id, 'enrolled', CURRENT_TIMESTAMP
                        FROM semesters s
                        WHERE s.is_active = true
                        LIMIT 1
                        """,
                        nativeQuery = true)
        int addEnrollment(Integer studentId, Integer courseId);
}
