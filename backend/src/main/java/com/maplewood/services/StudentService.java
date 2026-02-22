package com.maplewood.services;

import java.util.Objects;
import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.maplewood.domain.Course;
import com.maplewood.domain.Student;
import com.maplewood.repositories.StudentCourseHistoryRepository;
import com.maplewood.repositories.StudentRepository;

@Service
public class StudentService {
    private static final int MAX_COURSES_PER_SEMESTER = 5;

    private final StudentRepository studentRepository;
    private final StudentCourseHistoryRepository studentCourseHistoryRepository;

    public StudentService(StudentRepository studentRepository,
            StudentCourseHistoryRepository studentCourseHistoryRepository) {
        this.studentRepository = studentRepository;
        this.studentCourseHistoryRepository = studentCourseHistoryRepository;
    }

    public Student findStudentById(@NonNull Integer studentId) throws RuntimeException {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }


    /**
     * Determine if a student can take a course based on the following business rules:
     * 
     * <ol>
     * <li>The student must meet the grade level requirements of the course.</li>
     * <li>The student must not have already passed the course.</li>
     * <li>The student must have passed the prerequisite course, if any.</li>
     * <li>The student must not already be enrolled in the maximum number of courses for the active
     * semester.</li>
     * </ol>
     * 
     * @param student The student to check eligibility for.
     * @param course The course to check eligibility for.
     * @return An Optional containing a message code if the student cannot take the course, or empty
     *         if they can.
     */
    public Optional<EnrollmentErrorCode> canTakeCourse(Student student, Course course) {
        if (student == null || course == null || student.getId() == null || course.getId() == null
                || student.getGradeLevel() == null || course.getGradeLevelMin() == null
                || course.getGradeLevelMax() == null) {
            return Optional.of(EnrollmentErrorCode.INVALID_INPUT);
        }

        // Check if the student has the required grade level
        if (student.getGradeLevel() < course.getGradeLevelMin()
                || student.getGradeLevel() > course.getGradeLevelMax()) {
            return Optional.of(EnrollmentErrorCode.GRADE_LEVEL_MISMATCH);
        }

        // Check if the student has already passed this course
        var passedCourseIds = studentCourseHistoryRepository
                .findPassedCourseIdsByStudentId(Objects.requireNonNull(student.getId()));
        if (passedCourseIds.contains(course.getId())) {
            return Optional.of(EnrollmentErrorCode.COURSE_ALREADY_PASSED);
        }

        // Check if the student has taken the prerequisite course
        if (course.getPrerequisite() != null) {
            var prerequisiteCourseId = course.getPrerequisite().getId();

            if (prerequisiteCourseId == null || !passedCourseIds.contains(prerequisiteCourseId)) {
                return Optional.of(EnrollmentErrorCode.PREREQUISITE_NOT_MET);
            }
        }

        // Check if the student is already at the max number of courses in the active semester.
        if (studentCourseHistoryRepository.countActiveSemesterCoursesByStudentId(
                Objects.requireNonNull(student.getId())) >= MAX_COURSES_PER_SEMESTER) {
            return Optional.of(EnrollmentErrorCode.MAX_COURSES_REACHED);
        }

        // Time-slot conflicts are not validated here yet because the current domain model does not
        // include section/time-slot enrollment entities for active-semester schedules.
        return Optional.empty();
    }

    public enum EnrollmentErrorCode {
        INVALID_INPUT, GRADE_LEVEL_MISMATCH, COURSE_ALREADY_PASSED, PREREQUISITE_NOT_MET, MAX_COURSES_REACHED
    }
}
