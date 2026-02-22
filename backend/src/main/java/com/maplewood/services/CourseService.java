package com.maplewood.services;

import org.springframework.stereotype.Service;
import com.maplewood.repositories.CourseRepository;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StudentService studentService;

    public CourseService(CourseRepository courseRepository, StudentService studentService) {
        this.courseRepository = courseRepository;
        this.studentService = studentService;
    }

    /* Get all courses */
    public Page<Course> findAllCoursesLoaded(@NonNull Pageable pageable) {
        return courseRepository.findAllWithSpecializationAndPrerequisite(pageable);
    }

    /* Get courses for a specific semester */
    public Page<Course> findCoursesBySemesterOrder(@NonNull Pageable pageable) {
        var currentSemesterOrder = SemesterOrder.getCurrentSemesterOrder();
        return courseRepository.findAllBySemesterOrder(currentSemesterOrder, pageable);
    }


    /**
     * Get courses for a specific student.
     * <p>
     * This returns all the courses a user can take, depending on the business logic defined in the
     * student service.
     */
    public Page<Course> findCoursesForStudent(@NonNull Integer studentId,
            @NonNull Pageable pageable) {
        var student = studentService.findStudentById(studentId);
        var currentSemesterOrder = SemesterOrder.getCurrentSemesterOrder();
        var currentSemesterCourses = courseRepository
                .findAllCoursesBySemesterOrder(Objects.requireNonNull(currentSemesterOrder));
        var eligibleCoursesIds = currentSemesterCourses.stream()
                .filter(course -> studentService.canTakeCourse(student, course)).map(Course::getId)
                .toList();

        return courseRepository.findByIdIn(eligibleCoursesIds, pageable);
    }
}
