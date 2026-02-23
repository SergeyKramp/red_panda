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
import com.maplewood.domain.StudentStatus;
import com.maplewood.repositories.AppUserRepository;
import com.maplewood.repositories.StudentCourseHistoryRepository.CourseWithStatusProjection;
import com.maplewood.services.StudentCourseHistoryService;
import com.maplewood.services.StudentEnrollmentService;
import com.maplewood.services.StudentService;

@WebMvcTest(StudentDashboardController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class StudentDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentCourseHistoryService studentCourseHistoryService;
    @MockBean
    private StudentEnrollmentService studentEnrollmentService;
    @MockBean
    private StudentService studentService;

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
     * Given: no authenticated user
     * When: requesting the student dashboard info endpoint
     * Then: the response should be unauthorized (401)
     */
    @Test
    void givenNoAuthenticationWhenGettingInfoThenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/dashboard/student/info").accept(MediaType.APPLICATION_JSON))
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

    /**
     * Given: no authenticated user
     * When: requesting the student dashboard enrolled courses endpoint
     * Then: the response should be unauthorized (401)
     */
    @Test
    void givenNoAuthenticationWhenGettingEnrolledCoursesThenUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/dashboard/student/enrolled-courses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Given: an authenticated principal mapped to a student with active-semester enrollments
     * When: requesting the student dashboard enrolled courses endpoint
     * Then: the response should contain enrolledCourses with correctly mapped course fields
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedStudentWhenGettingEnrolledCoursesThenReturnsMappedResponse()
            throws Exception {
        var student = new Student();
        student.setId(7);

        var user = new AppUser();
        user.setUsername("test-user");
        user.setStudent(student);

        var english = new Course();
        english.setName("English Composition");
        english.setCredits(3.0);

        var biology = new Course();
        biology.setName("Biology I");
        biology.setCredits(2.0);

        when(appUserRepository.findByUsername("test-user")).thenReturn(Optional.of(user));
        when(studentEnrollmentService.getActiveSemesterEnrollments(7))
                .thenReturn(List.of(english, biology));

        mockMvc.perform(
                get("/api/dashboard/student/enrolled-courses").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrolledCourses.length()").value(2))
                .andExpect(jsonPath("$.enrolledCourses[0].courseName").value("English Composition"))
                .andExpect(jsonPath("$.enrolledCourses[0].credits").value("3.0"))
                .andExpect(jsonPath("$.enrolledCourses[1].courseName").value("Biology I"))
                .andExpect(jsonPath("$.enrolledCourses[1].credits").value("2.0"));

        verify(studentEnrollmentService).getActiveSemesterEnrollments(7);
    }

    /**
     * Given: an authenticated principal mapped to a student with dashboard information
     * When: requesting the student dashboard info endpoint
     * Then: the response should include student identity, grade level, status, and earned credits
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedStudentWhenGettingInfoThenReturnsMappedResponse()
            throws Exception {
        var student = new Student();
        student.setId(7);
        student.setFirstName("Emma");
        student.setLastName("Wilson");
        student.setEmail("emma.wilson@maplewood.edu");
        student.setGradeLevel(10);
        student.setStatus(StudentStatus.ACTIVE);

        var user = new AppUser();
        user.setUsername("test-user");
        user.setStudent(student);

        when(appUserRepository.findByUsername("test-user")).thenReturn(Optional.of(user));
        when(studentService.getStudentDashboardInformation(7))
                .thenReturn(new StudentService.StudentDashboardInformation(student, 18.0));

        mockMvc.perform(get("/api/dashboard/student/info").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Emma"))
                .andExpect(jsonPath("$.lastName").value("Wilson"))
                .andExpect(jsonPath("$.email").value("emma.wilson@maplewood.edu"))
                .andExpect(jsonPath("$.gradeLevel").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.earnedCredits").value(18.0));

        verify(studentService).getStudentDashboardInformation(7);
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
