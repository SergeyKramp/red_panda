package com.maplewood.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseType;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;

@DataJpaTest
@ActiveProfiles("test")
class SpecializationRepositoryTest {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SpecializationRepository specializationRepository;

    @Test
    void findSpecializationsByCourseIds() {
        var science = new Specialization();
        science.setName("Science");
        science = specializationRepository.save(science);

        var arts = new Specialization();
        arts.setName("Arts");
        arts = specializationRepository.save(arts);

        var chemistry = createCourse("SCI101", science);
        var drawing = createCourse("ART101", arts);
        chemistry = courseRepository.save(chemistry);
        drawing = courseRepository.save(drawing);

        var results = specializationRepository.findSpecializationsByCourseIds(
                java.util.List.of(chemistry.getId(), drawing.getId()));

        var map = results.stream()
                .collect(Collectors.toMap(
                        SpecializationRepository.CourseSpecializationProjection::getCourseId,
                        SpecializationRepository.CourseSpecializationProjection::getSpecialization));

        assertThat(map.get(chemistry.getId()).getId()).isEqualTo(science.getId());
        assertThat(map.get(chemistry.getId()).getName()).isEqualTo(science.getName());
        assertThat(map.get(drawing.getId()).getId()).isEqualTo(arts.getId());
        assertThat(map.get(drawing.getId()).getName()).isEqualTo(arts.getName());
    }

    private Course createCourse(String code, Specialization specialization) {
        var course = new Course();
        course.setCode(code);
        course.setName(code + " name");
        course.setCredits(3.0);
        course.setHoursPerWeek(3);
        course.setSemesterOrder(SemesterOrder.FALL);
        course.setCourseType(CourseType.CORE);
        course.setSpecialization(specialization);
        return course;
    }
}
