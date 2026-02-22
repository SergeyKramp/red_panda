package com.maplewood.services;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.maplewood.repositories.CourseRepository;
import com.maplewood.repositories.SpecializationRepository;
import com.maplewood.domain.Course;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

@Service
public class CourseService {
    final CourseRepository courseRepository;
    final SpecializationRepository specializationRepository;

    public CourseService(CourseRepository courseRepository,
            SpecializationRepository specializationRepository) {
        this.courseRepository = courseRepository;
        this.specializationRepository = specializationRepository;
    }

    /* Get all courses with pagination */
    public Page<Course> findAllCourses(@NonNull Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    /**
     * Gets a mapping from course ID to specialization.
     * <p>
     * This is used to avoid N+1 query problems when fetching courses and their specializations.
     */
    public Map<Integer, Specialization> findSpecializationsByCourseIds(Collection<Integer> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Map.of();
        }

        return specializationRepository.findSpecializationsByCourseIds(courseIds).stream()
                .collect(Collectors.toMap(
                        SpecializationRepository.CourseSpecializationProjection::getCourseId,
                        SpecializationRepository.CourseSpecializationProjection::getSpecialization));
    }

    /* Get courses for a specific semester */
    public Page<Course> findCoursesBySemesterOrder(SemesterOrder semesterOrder,
            @NonNull Pageable pageable) {
        return courseRepository.findAllBySemesterOrder(semesterOrder, pageable);
    }

}
