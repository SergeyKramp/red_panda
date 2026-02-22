package com.maplewood.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.maplewood.domain.Course;
import com.maplewood.domain.CourseType;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Given: 2 courses with one semester being for the spring and the other for the fall
     * 
     * When: findAllBySemesterOrder is called with the fall semester
     * 
     * Then: only the fall course should be returned
     */
    @Test
    void bySemesterOrder() {
        var specialization = new Specialization();
        specialization.setName("Science");
        specialization = entityManager.persistFlushFind(specialization);

        var springCourse = new Course();
        springCourse.setCode("SPR101");
        springCourse.setName("Spring Course");
        springCourse.setCredits(3.0);
        springCourse.setHoursPerWeek(3);
        springCourse.setSemesterOrder(SemesterOrder.SPRING);
        springCourse.setCourseType(CourseType.CORE);
        springCourse.setSpecialization(specialization);

        var fallCourse = new Course();
        fallCourse.setCode("FALL101");
        fallCourse.setName("Fall Course");
        fallCourse.setCredits(3.0);
        fallCourse.setHoursPerWeek(3);
        fallCourse.setSemesterOrder(SemesterOrder.FALL);
        fallCourse.setCourseType(CourseType.ELECTIVE);
        fallCourse.setSpecialization(specialization);

        courseRepository.save(springCourse);
        courseRepository.save(fallCourse);

        var pageable = PageRequest.of(0, 10, Sort.by("code").ascending());
        var fallCourses = courseRepository.findAllBySemesterOrder(SemesterOrder.FALL, pageable);
        assertThat(fallCourses.getContent()).hasSize(1);
    }

    /**
     * Given: a course
     * 
     * When: persisting the course with a course type of CORE and a semester order of SPRING
     * 
     * Then: the course should be persisted correctly with values of "core" and "2" respectively for
     * the course type and semester order
     */
    @Test
    void persistingCourseWithCoreTypeAndSpringSemesterOrder() {
        var specialization = new Specialization();
        specialization.setName("Science");
        specialization = entityManager.persistFlushFind(specialization);

        var course = new Course();
        course.setCode("SPR101");
        course.setName("Spring Course");
        course.setCredits(3.0);
        course.setHoursPerWeek(3);
        course.setSemesterOrder(SemesterOrder.SPRING);
        course.setCourseType(CourseType.CORE);
        course.setSpecialization(specialization);

        course = courseRepository.save(course);
        entityManager.flush();
        entityManager.clear();

        var persistedCourse = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(persistedCourse.getCourseType()).isEqualTo(CourseType.CORE);
        assertThat(persistedCourse.getSemesterOrder()).isEqualTo(SemesterOrder.SPRING);

        var rawRow = (Object[]) entityManager.getEntityManager()
                .createNativeQuery(
                        "SELECT course_type, semester_order FROM courses WHERE id = :id")
                .setParameter("id", course.getId())
                .getSingleResult();

        assertThat(rawRow[0]).isEqualTo("core");
        assertThat(((Number) rawRow[1]).intValue()).isEqualTo(SemesterOrder.SPRING.getCode());
    }
}
