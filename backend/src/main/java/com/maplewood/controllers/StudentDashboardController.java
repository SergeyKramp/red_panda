package com.maplewood.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.http.HttpStatus;
import com.maplewood.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import com.maplewood.services.StudentCourseHistoryService;
import com.maplewood.services.StudentEnrollmentService;
import com.maplewood.services.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/api/dashboard/student")
public class StudentDashboardController {

        private final StudentCourseHistoryService studentCourseHistoryService;
        private final StudentEnrollmentService studentEnrollmentService;
        private final StudentService studentService;
        private final AppUserRepository appUserRepository;

        public StudentDashboardController(StudentCourseHistoryService studentCourseHistoryService,
                        StudentEnrollmentService studentEnrollmentService,
                        StudentService studentService,
                        AppUserRepository appUserRepository) {
                this.studentCourseHistoryService = studentCourseHistoryService;
                this.studentEnrollmentService = studentEnrollmentService;
                this.studentService = studentService;
                this.appUserRepository = appUserRepository;
        }

        @GetMapping("/info")
        public ResponseEntity<StudentInformationResponse> getMetrics(Authentication authentication) {
                var studentId = getAuthenticatedStudentId(authentication);
                var studentDashboardInformation = studentService
                                .getStudentDashboardInformation(studentId);
                var student = studentDashboardInformation.student();

                return ResponseEntity.ok(new StudentInformationResponse(
                                student.getFirstName(),
                                student.getLastName(),
                                student.getEmail(),
                                student.getGradeLevel(),
                                student.getStatus() == null ? null : student.getStatus().name(),
                                studentDashboardInformation.earnedCredits()));
        }


        @GetMapping("/course-history")
        public ResponseEntity<CourseHistoryResponse> getCourseHistory(
                        Authentication authentication) {
                var studentId = getAuthenticatedStudentId(authentication);

                var courseHistory = studentCourseHistoryService.getStudentCourseHistory(studentId)
                                .stream()
                                .map(ch -> new CourseHistoryDTO(ch.getCourse().getName(),
                                                String.valueOf(ch.getCourse().getCredits()),
                                                ch.getStatus().name()))
                                .toList();

                return ResponseEntity.ok(new CourseHistoryResponse(courseHistory));

        }

        @GetMapping("/enrolled-courses")
        public ResponseEntity<EnrolledCoursesResponse> getEnrolledCourses(
                        Authentication authentication) {
                var studentId = getAuthenticatedStudentId(authentication);

                var enrolledCourses = studentEnrollmentService
                                .getActiveSemesterEnrollments(studentId).stream()
                                .map(course -> new EnrolledCourseDTO(course.getName(),
                                                String.valueOf(course.getCredits())))
                                .toList();

                return ResponseEntity.ok(new EnrolledCoursesResponse(enrolledCourses));
        }

        private Integer getAuthenticatedStudentId(Authentication authentication) {
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

                return student.getId();
        }

        record StudentInformationResponse(String firstName, String lastName, String email,
                        Integer gradeLevel, String status, Double earnedCredits) {
        }

        record CourseHistoryResponse(List<CourseHistoryDTO> courseHistory) {
        }

        record CourseHistoryDTO(String courseName, String credits, String status) {
        }

        record EnrolledCoursesResponse(List<EnrolledCourseDTO> enrolledCourses) {
        }

        record EnrolledCourseDTO(String courseName, String credits) {
        }



}
