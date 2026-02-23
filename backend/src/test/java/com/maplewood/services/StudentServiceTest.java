package com.maplewood.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.maplewood.domain.StudentEnrollmentStatus;
import com.maplewood.repositories.StudentCourseHistoryRepository;
import com.maplewood.repositories.StudentEnrollmentRepository;
import com.maplewood.repositories.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StudentCourseHistoryRepository studentCourseHistoryRepository;
    @Mock
    private StudentEnrollmentRepository studentEnrollmentRepository;

    @InjectMocks
    private StudentService studentService;

    /**
     * Given: a student that exists and repository-calculated earned credits
     *
     * When: getStudentDashboardInformation is called
     *
     * Then: the response should include both the student and earned credits
     */
    @Test
    void getStudentDashboardInformationReturnsStudentAndEarnedCredits() {
        var student = buildStudent(7, 11);

        when(studentRepository.findById(7)).thenReturn(java.util.Optional.of(student));
        when(studentCourseHistoryRepository.findEarnedCreditsByStudentId(7)).thenReturn(18.0);

        var dashboardInformation = studentService.getStudentDashboardInformation(7);

        assertThat(dashboardInformation.student().getId()).isEqualTo(7);
        assertThat(dashboardInformation.earnedCredits()).isEqualTo(18.0);
        verify(studentRepository).findById(7);
        verify(studentCourseHistoryRepository).findEarnedCreditsByStudentId(7);
    }

    /**
     * Given: a student id that does not exist
     *
     * When: getStudentDashboardInformation is called
     *
     * Then: it should throw a student-not-found exception and not query earned credits
     */
    @Test
    void getStudentDashboardInformationThrowsWhenStudentNotFound() {
        when(studentRepository.findById(99)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentDashboardInformation(99))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository).findById(99);
        verify(studentCourseHistoryRepository, never()).findEarnedCreditsByStudentId(99);
    }

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

        assertThat(eligible).isPresent();
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

        assertThat(eligible).isPresent();
    }

    /**
     * Given: a student currently enrolled in the target course
     *
     * When: canTakeCourse is called
     *
     * Then: eligibility should be rejected to prevent duplicate active enrollment
     */
    @Test
    void canTakeCourseRejectsAlreadyEnrolledCourse() {
        var student = buildStudent(1, 10);
        var course = buildCourse(101, 9, 12, null);

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1)).thenReturn(Set.of());
        when(studentEnrollmentRepository.existsByStudentIdAndCourseIdAndStatus(1, 101,
                StudentEnrollmentStatus.ENROLLED)).thenReturn(true);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).contains(StudentService.EnrollmentErrorCode.COURSE_ALREADY_ENROLLED);
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

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1)).thenReturn(Set.of());
        when(studentEnrollmentRepository.existsByStudentIdAndCourseIdAndStatus(1, 101,
                StudentEnrollmentStatus.ENROLLED)).thenReturn(false);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isPresent();
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

        when(studentCourseHistoryRepository.findPassedCourseIdsByStudentId(1)).thenReturn(Set.of());
        when(studentEnrollmentRepository.existsByStudentIdAndCourseIdAndStatus(1, 101,
                StudentEnrollmentStatus.ENROLLED)).thenReturn(false);
        when(studentEnrollmentRepository.countEnrolledCoursesByStudentIdInActiveSemester(1))
                .thenReturn(5L);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isPresent();
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
        when(studentEnrollmentRepository.existsByStudentIdAndCourseIdAndStatus(1, 101,
                StudentEnrollmentStatus.ENROLLED)).thenReturn(false);
        when(studentEnrollmentRepository.countEnrolledCoursesByStudentIdInActiveSemester(1))
                .thenReturn(3L);

        var eligible = studentService.canTakeCourse(student, course);

        assertThat(eligible).isEmpty();
    }

    private Student buildStudent(Integer id, Integer gradeLevel) {
        var student = new Student();
        student.setId(id);
        student.setGradeLevel(gradeLevel);
        return student;
    }

    private Course buildCourse(Integer id, Integer gradeLevelMin, Integer gradeLevelMax,
            Course prerequisite) {
        var course = new Course();
        course.setId(id);
        course.setGradeLevelMin(gradeLevelMin);
        course.setGradeLevelMax(gradeLevelMax);
        course.setPrerequisite(prerequisite);
        return course;
    }
}
