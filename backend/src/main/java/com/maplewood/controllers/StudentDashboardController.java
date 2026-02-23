package com.maplewood.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.http.HttpStatus;
import com.maplewood.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import com.maplewood.services.StudentCourseHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/dashboard/student")
public class StudentDashboardController {

        private final StudentCourseHistoryService studentCourseHistoryService;
        private final AppUserRepository appUserRepository;

        public StudentDashboardController(StudentCourseHistoryService studentCourseHistoryService,
                        AppUserRepository appUserRepository) {
                this.studentCourseHistoryService = studentCourseHistoryService;
                this.appUserRepository = appUserRepository;
        }

        @GetMapping("/course-history")
        public ResponseEntity<CourseHistoryResponse> getCourseHistory(
                        Authentication authentication) {
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


                var courseHistory = studentCourseHistoryService
                                .getStudentCourseHistory(student.getId()).stream()
                                .map(ch -> new CourseHistoryDTO(ch.getCourse().getName(),
                                                String.valueOf(ch.getCourse().getCredits()),
                                                ch.getStatus().name()))
                                .toList();

                return ResponseEntity.ok(new CourseHistoryResponse(courseHistory));

        }

        record CourseHistoryResponse(List<CourseHistoryDTO> courseHistory) {
        }

        record CourseHistoryDTO(String courseName, String credits, String status) {
        }

}
