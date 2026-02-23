package com.maplewood.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.maplewood.repositories.AppUserRepository;
import com.maplewood.services.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

        private final CourseService courseService;
        private final AppUserRepository appUserRepository;
        // There aren't that many courses in the demo database, so just get all of them.
        // No pagination of the frontend for now.
        private final Pageable pageable = PageRequest.of(0, 100);

        public CourseController(CourseService courseService, AppUserRepository appUserRepository) {
                this.courseService = courseService;
                this.appUserRepository = appUserRepository;
        }

        @GetMapping("/")
        public ResponseEntity<List<CourseDTO>> getCourses() {
                var courses = courseService.findAllCoursesLoaded(this.pageable);

                var courseDTOs = courses.getContent().stream()
                                .map(course -> new CourseDTO(course.getId(), course.getCode(),
                                                course.getName(), course.getDescription(),
                                                course.getCredits(), course.getHoursPerWeek(),
                                                course.getSpecialization().getName(),
                                                course.getPrerequisite() != null
                                                                ? course.getPrerequisite().getName()
                                                                : null,
                                                course.getCourseType().name(),
                                                course.getGradeLevelMin(),
                                                course.getGradeLevelMax()))
                                .toList();
                return ResponseEntity.ok(courseDTOs);
        }

        @GetMapping("/semester")
        public ResponseEntity<List<CourseDTO>> getCoursesBySemester() {
                var courses = courseService.findCoursesForActiveSemester(this.pageable);

                var courseDTOs = courses.getContent().stream()
                                .map(course -> new CourseDTO(course.getId(), course.getCode(),
                                                course.getName(), course.getDescription(),
                                                course.getCredits(), course.getHoursPerWeek(),
                                                course.getSpecialization().getName(),
                                                course.getPrerequisite() != null
                                                                ? course.getPrerequisite().getName()
                                                                : null,
                                                course.getCourseType().name(),
                                                course.getGradeLevelMin(),
                                                course.getGradeLevelMax()))
                                .toList();
                return ResponseEntity.ok(courseDTOs);
        }

        @GetMapping("/student")
        public ResponseEntity<List<CourseDTO>> getCoursesForStudent(Authentication authentication) {
                var username = authentication.getName();

                var user = appUserRepository.findByUsername(username)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Authenticated user not found"));
                var student = user.getStudent();
                if (student == null || student.getId() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Authenticated user is not linked to a student");
                }

                var courses = courseService.findCoursesForStudent(student.getId(), this.pageable);

                var courseDTOs = courses.getContent().stream()
                                .map(course -> new CourseDTO(course.getId(), course.getCode(),
                                                course.getName(), course.getDescription(),
                                                course.getCredits(), course.getHoursPerWeek(),
                                                course.getSpecialization().getName(),
                                                course.getPrerequisite() != null
                                                                ? course.getPrerequisite().getName()
                                                                : null,
                                                course.getCourseType().name(),
                                                course.getGradeLevelMin(),
                                                course.getGradeLevelMax()))
                                .toList();
                return ResponseEntity.ok(courseDTOs);
        }

        @PostMapping("/enroll/c/{id}")
        public ResponseEntity<EnrollmentErrorResponse> enrollInCourse(Authentication authentication,
                        @PathVariable Integer id) {
                var username = authentication.getName();
                var user = appUserRepository.findByUsername(username)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED,
                                                "Authenticated user not found"));
                var student = user.getStudent();
                if (student == null || student.getId() == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Authenticated user is not linked to a student");
                }

                Optional<String> messageCode;
                try {
                        messageCode = courseService.enrollStudentInCourse(student, id);
                } catch (RuntimeException e) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
                }

                if (messageCode.isPresent()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body(new EnrollmentErrorResponse(messageCode.get(), id));
                }

                return ResponseEntity.ok().build();
        }

}


record EnrollmentErrorResponse(String messageCode, Integer courseId) {
}


record CourseDTO(Integer id, String code, String name, String description, Double credits,
                Integer hoursPerWeek, String specialization, String prerequisite, String courseType,
                Integer gradeLevelMin, Integer gradeLevelMax) {
}
