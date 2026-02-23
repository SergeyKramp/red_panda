package com.maplewood.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.maplewood.domain.Course;
import com.maplewood.domain.Semester;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Student;
import com.maplewood.repositories.CourseRepository;
import com.maplewood.repositories.SemesterRepository;
import com.maplewood.repositories.StudentEnrollmentRepository;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private StudentEnrollmentRepository studentEnrollmentRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @InjectMocks
    private CourseService courseService;

    /**
     * Given: an active semester in the database with FALL order
     *
     * When: findCoursesForActiveSemester is called
     *
     * Then: courses should be fetched using the active semester order from DB
     */
    @Test
    void findCoursesForActiveSemesterUsesActiveSemesterFromDatabase() {
        var activeSemester = new Semester();
        activeSemester.setOrderInYear(SemesterOrder.FALL);

        var pageable = PageRequest.of(0, 10);
        var expectedPage = new PageImpl<Course>(List.of());

        when(semesterRepository.findFirstByActiveTrueOrderByYearDescOrderInYearDesc())
                .thenReturn(Optional.of(activeSemester));
        when(courseRepository.findAllBySemesterOrder(SemesterOrder.FALL, pageable))
                .thenReturn(expectedPage);

        var result = courseService.findCoursesForActiveSemester(pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(courseRepository).findAllBySemesterOrder(SemesterOrder.FALL, pageable);
    }

    /**
     * Given: no active semester in the database
     *
     * When: findCoursesForActiveSemester is called
     *
     * Then: a clear exception should be thrown
     */
    @Test
    void findCoursesForActiveSemesterThrowsWhenNoActiveSemesterExists() {
        when(semesterRepository.findFirstByActiveTrueOrderByYearDescOrderInYearDesc())
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.findCoursesForActiveSemester(PageRequest.of(0, 10)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No active semester found for courses");
    }

    /**
     * Given: an active semester and two offered courses where only one is eligible
     *
     * When: findCoursesForStudent is called
     *
     * Then: only eligible active-semester courses should be returned
     */
    @Test
    void findCoursesForStudentFiltersEligibleCoursesFromActiveSemester() {
        var student = new Student();
        student.setId(7);

        var activeSemester = new Semester();
        activeSemester.setOrderInYear(SemesterOrder.SPRING);

        var eligibleCourse = new Course();
        eligibleCourse.setId(10);

        var ineligibleCourse = new Course();
        ineligibleCourse.setId(20);

        var pageable = PageRequest.of(0, 10);
        var expectedPage = new PageImpl<Course>(List.of(eligibleCourse));

        when(studentService.findStudentById(7)).thenReturn(student);
        when(semesterRepository.findFirstByActiveTrueOrderByYearDescOrderInYearDesc())
                .thenReturn(Optional.of(activeSemester));
        when(courseRepository.findAllCoursesBySemesterOrder(SemesterOrder.SPRING))
                .thenReturn(List.of(eligibleCourse, ineligibleCourse));
        when(studentService.canTakeCourse(student, eligibleCourse)).thenReturn(Optional.empty());
        when(studentService.canTakeCourse(student, ineligibleCourse))
                .thenReturn(Optional.of(StudentService.EnrollmentErrorCode.PREREQUISITE_NOT_MET));
        when(courseRepository.findByIdIn(eq(List.of(10)), any())).thenReturn(expectedPage);

        var result = courseService.findCoursesForStudent(7, pageable);

        assertThat(result).isEqualTo(expectedPage);
        verify(courseRepository).findAllCoursesBySemesterOrder(SemesterOrder.SPRING);
        verify(courseRepository).findByIdIn(eq(List.of(10)), any());
    }
}
