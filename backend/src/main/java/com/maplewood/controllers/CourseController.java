package com.maplewood.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maplewood.services.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @RequestMapping("/")
    public ResponseEntity<List<CourseDTO>> getCourses() {
        // There aren't that many courses in the demo database, so just get all of them. No
        // pagination of the frontend for now.
        var pageable = org.springframework.data.domain.PageRequest.of(0, 100);
        var courses = courseService.findAllCoursesLoaded(pageable);

        var courseDTOs = courses.getContent().stream()
                .map(course -> new CourseDTO(course.getId(), course.getCode(), course.getName(),
                        course.getDescription(), course.getCredits(), course.getHoursPerWeek(),
                        course.getSpecialization().getName(),
                        course.getPrerequisite() != null ? course.getPrerequisite().getName()
                                : null,
                        course.getCourseType().name(), course.getGradeLevelMin(),
                        course.getGradeLevelMax()))
                .toList();
        return ResponseEntity.ok(courseDTOs);
    }
}


record CourseDTO(Integer id, String code, String name, String description, Double credits,
        Integer hoursPerWeek, String specialization, String prerequisite, String courseType,
        Integer gradeLevelMin, Integer gradeLevelMax) {
}
