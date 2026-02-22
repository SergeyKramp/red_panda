package com.maplewood.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.maplewood.domain.Course;
import com.maplewood.domain.Student;
import com.maplewood.repositories.StudentCourseHistoryRepository;
import com.maplewood.repositories.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentCourseHistoryRepository studentCourseHistoryRepository;

    @InjectMocks
    private StudentService studentService;

    /**
     * Given: a student outside a course grade range
     *
     * When: canTakeCourse is called
     *
     * Then: eligibility should be rejected before querying course history
     */
    @Test
    void canTakeCourseRejectsGradeLevelOutsideRange() {
        var student = buildStudent(1, 9);
        var course = buildCourse(101, 10, 12, null);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isFalse();
        verify(studentCourseHistoryRepository, never()).findPassedCourseIdsByStudentId(1);
    }

    /**
     * Given: a student who has already passed the target course
     *
     * When: canTakeCourse is called
     *
     * Then: eligibility should be rejected to prevent duplicate enrollment
     */
    @Test
    void canTakeCourseRejectsAlreadyPassedCourse() {
        var student = buildStudent(1, 10);
        var course = buildCourse(101, 9, 12, null);

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1))
                .thenReturn(Set.of(101));

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isFalse();
    }

    /**
     * Given: a course with a prerequisite and a student who did not pass it
     *
     * When: canTakeCourse is called
     *
     * Then: eligibility should be rejected
     */
    @Test
    void canTakeCourseRejectsMissingPrerequisite() {
        var prerequisite = buildCourse(100, 9, 12, null);
        var student = buildStudent(1, 10);
        var course = buildCourse(101, 9, 12, prerequisite);

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1))
                .thenReturn(Set.of());

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isFalse();
    }

    /**
     * Given: a student already enrolled in five active-semester courses
     *
     * When: canTakeCourse is called for another course
     *
     * Then: eligibility should be rejected due to course load limit
     */
    @Test
    void canTakeCourseRejectsWhenActiveSemesterLimitReached() {
        var student = buildStudent(1, 10);
        var course = buildCourse(101, 9, 12, null);

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1))
                .thenReturn(Set.of());
        when(studentCourseHistoryRepository.countActiveSemesterCoursesByStudentId(1))
                .thenReturn(5L);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isFalse();
    }

    /**
     * Given: a student in-range, with prerequisites passed, and below active-semester load limit
     *
     * When: canTakeCourse is called
     *
     * Then: eligibility should be accepted
     */
    @Test
    void canTakeCourseAcceptsWhenAllChecksPass() {
        var prerequisite = buildCourse(100, 9, 12, null);
        var student = buildStudent(1, 10);
        var course = buildCourse(101, 9, 12, prerequisite);

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1))
                .thenReturn(Set.of(100));
        when(studentCourseHistoryRepository.countActiveSemesterCoursesByStudentId(1))
                .thenReturn(3L);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isTrue();
    }

    private Student buildStudent(Integer id, Integer gradeLevel) {
        var student = new Student();
        student.setId(id);
        student.setGradeLevel(gradeLevel);
        return student;
    }

    private Course buildCourse(
            Integer id,
            Integer gradeLevelMin,
            Integer gradeLevelMax,
            Course prerequisite) {
        var course = new Course();
        course.setId(id);
        course.setGradeLevelMin(gradeLevelMin);
        course.setGradeLevelMax(gradeLevelMax);
        course.setPrerequisite(prerequisite);
        return course;
    }
}
