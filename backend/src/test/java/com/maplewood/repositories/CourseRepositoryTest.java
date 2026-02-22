package com.maplewood.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import org.hibernate.Hibernate;
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
        assertThat(fallCourses.getContent()).extracting(Course::getCode).containsExactly("FALL101");
        assertThat(fallCourses.getContent()).extracting(Course::getCode).doesNotContain("SPR101");
    }

    /**
     * Given: fall and spring courses where a fall course has a prerequisite
     *
     * When: findAllBySemesterOrder is called with the fall semester
     *
     * Then: only fall courses are returned and specialization/prerequisite are eagerly loaded
     */
    @Test
    void bySemesterOrderLoadsAssociationsAndExcludesOtherOrders() {
        var specialization = new Specialization();
        specialization.setName("Science");
        specialization = entityManager.persistFlushFind(specialization);

        var fallPrerequisite = new Course();
        fallPrerequisite.setCode("BIO100");
        fallPrerequisite.setName("Biology Basics");
        fallPrerequisite.setCredits(3.0);
        fallPrerequisite.setHoursPerWeek(3);
        fallPrerequisite.setSemesterOrder(SemesterOrder.FALL);
        fallPrerequisite.setCourseType(CourseType.CORE);
        fallPrerequisite.setSpecialization(specialization);
        fallPrerequisite = courseRepository.save(fallPrerequisite);

        var fallAdvanced = new Course();
        fallAdvanced.setCode("BIO200");
        fallAdvanced.setName("Advanced Biology");
        fallAdvanced.setCredits(3.0);
        fallAdvanced.setHoursPerWeek(3);
        fallAdvanced.setSemesterOrder(SemesterOrder.FALL);
        fallAdvanced.setCourseType(CourseType.CORE);
        fallAdvanced.setSpecialization(specialization);
        fallAdvanced.setPrerequisite(fallPrerequisite);
        courseRepository.save(fallAdvanced);

        var springCourse = new Course();
        springCourse.setCode("SPR101");
        springCourse.setName("Spring Course");
        springCourse.setCredits(3.0);
        springCourse.setHoursPerWeek(3);
        springCourse.setSemesterOrder(SemesterOrder.SPRING);
        springCourse.setCourseType(CourseType.ELECTIVE);
        springCourse.setSpecialization(specialization);
        courseRepository.save(springCourse);

        entityManager.flush();
        entityManager.clear();

        var pageable = PageRequest.of(0, 10, Sort.by("code").ascending());
        var fallCourses = courseRepository.findAllBySemesterOrder(SemesterOrder.FALL, pageable);

        assertThat(fallCourses.getContent()).extracting(Course::getCode).containsExactly("BIO100",
                "BIO200");
        assertThat(fallCourses.getContent()).extracting(Course::getCode).doesNotContain("SPR101");

        var loadedAdvancedCourse = fallCourses.getContent().stream()
                .filter(course -> course.getCode().equals("BIO200")).findFirst().orElseThrow();

        assertThat(Hibernate.isInitialized(loadedAdvancedCourse.getSpecialization())).isTrue();
        assertThat(Hibernate.isInitialized(loadedAdvancedCourse.getPrerequisite())).isTrue();
    }

    /**
     * Given: courses with specialization and prerequisite relations
     *
     * When: findAllWithSpecializationAndPrerequisite is called
     *
     * Then: specialization and prerequisite are eagerly loaded on returned courses
     */
    @Test
    void findAllWithSpecializationAndPrerequisiteLoadsAssociations() {
        var specialization = new Specialization();
        specialization.setName("Science");
        specialization = entityManager.persistFlushFind(specialization);

        var prerequisiteCourse = new Course();
        prerequisiteCourse.setCode("BIO100");
        prerequisiteCourse.setName("Biology Basics");
        prerequisiteCourse.setCredits(3.0);
        prerequisiteCourse.setHoursPerWeek(3);
        prerequisiteCourse.setSemesterOrder(SemesterOrder.FALL);
        prerequisiteCourse.setCourseType(CourseType.CORE);
        prerequisiteCourse.setSpecialization(specialization);
        prerequisiteCourse = courseRepository.save(prerequisiteCourse);

        var advancedCourse = new Course();
        advancedCourse.setCode("BIO200");
        advancedCourse.setName("Advanced Biology");
        advancedCourse.setCredits(3.0);
        advancedCourse.setHoursPerWeek(3);
        advancedCourse.setSemesterOrder(SemesterOrder.SPRING);
        advancedCourse.setCourseType(CourseType.CORE);
        advancedCourse.setSpecialization(specialization);
        advancedCourse.setPrerequisite(prerequisiteCourse);
        courseRepository.save(advancedCourse);

        entityManager.flush();
        entityManager.clear();

        var pageable = PageRequest.of(0, 10, Sort.by("code").ascending());
        var courses = courseRepository.findAllWithSpecializationAndPrerequisite(pageable);
        var loadedAdvancedCourse = courses.getContent().stream()
                .filter(course -> course.getCode().equals("BIO200")).findFirst().orElseThrow();

        assertThat(Hibernate.isInitialized(loadedAdvancedCourse.getSpecialization())).isTrue();
        assertThat(Hibernate.isInitialized(loadedAdvancedCourse.getPrerequisite())).isTrue();
        assertThat(loadedAdvancedCourse.getSpecialization().getName()).isEqualTo("Science");
        assertThat(loadedAdvancedCourse.getPrerequisite().getCode()).isEqualTo("BIO100");
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
                .createNativeQuery("SELECT course_type, semester_order FROM courses WHERE id = :id")
                .setParameter("id", course.getId()).getSingleResult();

        assertThat(rawRow[0]).isEqualTo("core");
        assertThat(((Number) rawRow[1]).intValue()).isEqualTo(SemesterOrder.SPRING.getCode());
    }
}
