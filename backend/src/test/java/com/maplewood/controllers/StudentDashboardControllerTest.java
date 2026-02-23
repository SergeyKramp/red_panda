package com.maplewood.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.maplewood.config.SecurityConfig;
import com.maplewood.domain.AppUser;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseHistoryStatus;
import com.maplewood.domain.Student;
import com.maplewood.repositories.AppUserRepository;
import com.maplewood.repositories.StudentCourseHistoryRepository.CourseWithStatusProjection;
import com.maplewood.services.StudentCourseHistoryService;

@WebMvcTest(StudentDashboardController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class StudentDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentCourseHistoryService studentCourseHistoryService;

    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    /**
     * Given: no authenticated user When: requesting the student dashboard course history endpoint
     * Then: the response should be unauthorized (401)
     */
    @Test
    void givenNoAuthenticationWhenGettingCourseHistoryThenUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/dashboard/student/course-history").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Given: an authenticated principal with no matching application user record When: requesting
     * the student dashboard course history endpoint Then: the response should be unauthorized (401)
     * and course history should not be queried
     */
    @Test
    @WithMockUser(username = "missing-user")
    void givenAuthenticatedUserNotFoundWhenGettingCourseHistoryThenUnauthorized() throws Exception {
        when(appUserRepository.findByUsername("missing-user")).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/dashboard/student/course-history").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(studentCourseHistoryService);
    }

    /**
     * Given: an authenticated principal mapped to a user without a linked student id When:
     * requesting the student dashboard course history endpoint Then: the response should be bad
     * request (400) and course history should not be queried
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedUserWithoutStudentWhenGettingCourseHistoryThenBadRequest()
            throws Exception {
        var user = new AppUser();
        user.setUsername("test-user");

        when(appUserRepository.findByUsername("test-user")).thenReturn(Optional.of(user));

        mockMvc.perform(
                get("/api/dashboard/student/course-history").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentCourseHistoryService);
    }

    /**
     * Given: an authenticated principal mapped to a student with course history When: requesting
     * the student dashboard course history endpoint Then: the response should contain a
     * courseHistory array with correctly mapped fields
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedStudentWhenGettingCourseHistoryThenReturnsMappedResponse()
            throws Exception {
        var student = new Student();
        student.setId(7);

        var user = new AppUser();
        user.setUsername("test-user");
        user.setStudent(student);

        var english = new Course();
        english.setName("English Composition");
        english.setCredits(3.0);

        when(appUserRepository.findByUsername("test-user")).thenReturn(Optional.of(user));
        when(studentCourseHistoryService.getStudentCourseHistory(7))
                .thenReturn(List.of(courseProjection(english, CourseHistoryStatus.PASSED)));

        mockMvc.perform(
                get("/api/dashboard/student/course-history").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.courseHistory.length()").value(1))
                .andExpect(jsonPath("$.courseHistory[0].courseName").value("English Composition"))
                .andExpect(jsonPath("$.courseHistory[0].credits").value("3.0"))
                .andExpect(jsonPath("$.courseHistory[0].status").value("PASSED"));

        verify(studentCourseHistoryService).getStudentCourseHistory(7);
    }

    private CourseWithStatusProjection courseProjection(Course course, CourseHistoryStatus status) {
        return new CourseWithStatusProjection() {
            @Override
            public Course getCourse() {
                return course;
            }

            @Override
            public CourseHistoryStatus getStatus() {
                return status;
            }
        };
    }
}
