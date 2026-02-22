package com.maplewood.services;

import org.springframework.stereotype.Service;
import com.maplewood.repositories.CourseRepository;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

@Service
public class CourseService {
    final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /* Get all courses */
    public Page<Course> findAllCoursesLoaded(@NonNull Pageable pageable) {
        return courseRepository.findAllWithSpecializationAndPrerequisite(pageable);
    }

    /* Get courses for a specific semester */
    public Page<Course> findCoursesBySemesterOrder(SemesterOrder semesterOrder,
            @NonNull Pageable pageable) {
        return courseRepository.findAllBySemesterOrder(semesterOrder, pageable);
    }

}
