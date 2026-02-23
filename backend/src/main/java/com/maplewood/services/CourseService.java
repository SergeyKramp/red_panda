package com.maplewood.services;

import org.springframework.stereotype.Service;
import com.maplewood.domain.Semester;
import com.maplewood.repositories.CourseRepository;
import com.maplewood.repositories.SemesterRepository;
import com.maplewood.repositories.StudentEnrollmentRepository;
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
    private final SemesterRepository semesterRepository;
    private final StudentService studentService;
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public CourseService(
            CourseRepository courseRepository,
            SemesterRepository semesterRepository,
            StudentService studentService,
            StudentEnrollmentRepository studentEnrollmentRepository) {
        this.courseRepository = courseRepository;
        this.semesterRepository = semesterRepository;
        this.studentService = studentService;
        this.studentEnrollmentRepository = studentEnrollmentRepository;
    }

    /* Get all courses */
    public Page<Course> findAllCoursesLoaded(@NonNull Pageable pageable) {
        return courseRepository.findAllWithSpecializationAndPrerequisite(pageable);
    }

    /* Get courses for the active semester */
    public Page<Course> findCoursesForActiveSemester(@NonNull Pageable pageable) {
        var activeSemesterOrder = getActiveSemesterOrder();
        return courseRepository.findAllBySemesterOrder(activeSemesterOrder, pageable);
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
        var activeSemesterOrder = getActiveSemesterOrder();
        var activeSemesterCourses = courseRepository.findAllCoursesBySemesterOrder(
                activeSemesterOrder);
        var eligibleCoursesIds = activeSemesterCourses.stream()
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
            var affectedRows = studentEnrollmentRepository.addEnrollment(
                    Objects.requireNonNull(student.getId()),
                    Objects.requireNonNull(course.get().getId()));
            if (affectedRows == 0) {
                throw new RuntimeException("No active semester found for enrollment");
            }

            return Optional.empty();
        }



    }

    private SemesterOrder getActiveSemesterOrder() {
        return semesterRepository.findFirstByActiveTrueOrderByYearDescOrderInYearDesc()
                .map(Semester::getOrderInYear)
                .filter(Objects::nonNull)
                .orElseThrow(() -> new RuntimeException("No active semester found for courses"));
    }
}
