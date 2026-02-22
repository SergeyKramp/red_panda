package com.maplewood.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.maplewood.config.SecurityConfig;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseType;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;
import com.maplewood.services.CourseService;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;
    @MockBean
    private JdbcTemplate jdbcTemplate;

    /**
     * Given: no authenticated user
     * When: requesting all courses from the API
     * Then: the response should be unauthorized (401)
     */
    @Test
    void givenNoAuthenticationWhenGettingAllCoursesThenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/courses/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Given: no authenticated user
     * When: requesting semester-filtered courses from the API
     * Then: the response should be unauthorized (401)
     */
    @Test
    void givenNoAuthenticationWhenGettingSemesterCoursesThenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/courses/semester").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Given: an authenticated user and multiple courses in the service response
     * When: requesting all courses from the API
     * Then: the response should contain the mapped course data including specialization and
     * prerequisite names
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedUserWhenGettingAllCoursesThenReturnsProperResponse() throws Exception {
        var science = new Specialization();
        science.setId(1);
        science.setName("Science");

        var math = new Specialization();
        math.setId(2);
        math.setName("Mathematics");

        var introBiology = createCourse(1, "BIO100", "Intro Biology", "Biology intro", 3.0, 3,
                science, null, CourseType.CORE, 9, 10, SemesterOrder.FALL);
        var algebraTwo = createCourse(2, "MTH201", "Algebra II", "Advanced algebra", 3.0, 4, math,
                introBiology, CourseType.CORE, 10, 11, SemesterOrder.SPRING);

        when(courseService.findAllCoursesLoaded(any()))
                .thenReturn(new PageImpl<>(List.of(introBiology, algebraTwo), PageRequest.of(0, 100),
                        2));

        mockMvc.perform(get("/api/courses/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("BIO100"))
                .andExpect(jsonPath("$[0].specialization").value("Science"))
                .andExpect(jsonPath("$[0].prerequisite").value(nullValue()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].code").value("MTH201"))
                .andExpect(jsonPath("$[1].specialization").value("Mathematics"))
                .andExpect(jsonPath("$[1].prerequisite").value("Intro Biology"));
    }

    /**
     * Given: an authenticated user and a course with no prerequisite
     * When: requesting all courses from the API
     * Then: the course prerequisite field should be null in the response
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenCourseWithoutPrerequisiteWhenGettingAllCoursesThenPrerequisiteIsNull() throws Exception {
        var science = new Specialization();
        science.setId(1);
        science.setName("Science");

        var introBiology = createCourse(1, "BIO100", "Intro Biology", "Biology intro", 3.0, 3,
                science, null, CourseType.CORE, 9, 10, SemesterOrder.FALL);

        when(courseService.findAllCoursesLoaded(any()))
                .thenReturn(new PageImpl<>(List.of(introBiology), PageRequest.of(0, 100), 1));

        mockMvc.perform(get("/api/courses/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].code").value("BIO100"))
                .andExpect(jsonPath("$[0].prerequisite").value(nullValue()));
    }

    /**
     * Given: an authenticated user and semester courses in the service response
     * When: requesting semester-filtered courses from the API
     * Then: the response should contain the mapped course data and call the semester service
     * method
     */
    @Test
    @WithMockUser(username = "test-user")
    void givenAuthenticatedUserWhenGettingSemesterCoursesThenReturnsProperResponse()
            throws Exception {
        var science = new Specialization();
        science.setId(1);
        science.setName("Science");

        var introBiology = createCourse(1, "BIO100", "Intro Biology", "Biology intro", 3.0, 3,
                science, null, CourseType.CORE, 9, 10, SemesterOrder.SPRING);

        when(courseService.findCoursesBySemesterOrder(any()))
                .thenReturn(new PageImpl<>(List.of(introBiology), PageRequest.of(0, 100), 1));

        mockMvc.perform(get("/api/courses/semester").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].code").value("BIO100"))
                .andExpect(jsonPath("$[0].specialization").value("Science"))
                .andExpect(jsonPath("$[0].prerequisite").value(nullValue()));

        verify(courseService).findCoursesBySemesterOrder(any());
    }

    private Course createCourse(Integer id, String code, String name, String description, Double credits,
            Integer hoursPerWeek, Specialization specialization, Course prerequisite,
            CourseType courseType, Integer gradeLevelMin, Integer gradeLevelMax,
            SemesterOrder semesterOrder) {
        var course = new Course();
        course.setId(id);
        course.setCode(code);
        course.setName(name);
        course.setDescription(description);
        course.setCredits(credits);
        course.setHoursPerWeek(hoursPerWeek);
        course.setSpecialization(specialization);
        course.setPrerequisite(prerequisite);
        course.setCourseType(courseType);
        course.setGradeLevelMin(gradeLevelMin);
        course.setGradeLevelMax(gradeLevelMax);
        course.setSemesterOrder(semesterOrder);
        return course;
    }
}
