package com.maplewood.services;

import org.springframework.stereotype.Service;
import com.maplewood.repositories.CourseRepository;
import jakarta.transaction.Transactional;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Student;
import java.util.Objects;
import java.util.Optional;
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
                .filter(course -> studentService.canTakeCourse(student, course).isEmpty())
                .map(Course::getId).toList();

        return courseRepository.findByIdIn(eligibleCoursesIds, pageable);
    }

    @Transactional
    public Optional<String> enrollStudentInCourse(Student student, Integer courseId)
            throws RuntimeException {
        var course = courseRepository.findById(Objects.requireNonNull(courseId));
        if (course.isEmpty()) {
            throw new RuntimeException("Course not found");
        }

        var messageCodeOpt = studentService.canTakeCourse(student, course.get());

        if (messageCodeOpt.isPresent()) {
            return messageCodeOpt.map(Enum::name); // Convert the EnrollmentErrorCode to its name as
                                                   // a string
        } else {

            return Optional.empty();
        }



    }
}
