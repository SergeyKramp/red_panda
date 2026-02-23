package com.maplewood.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.maplewood.repositories.StudentCourseHistoryRepository;
import com.maplewood.repositories.StudentCourseHistoryRepository.CourseWithStatusProjection;

@Service
public class StudentCourseHistoryService {
    private final StudentCourseHistoryRepository studentCourseHistoryRepository;

    public StudentCourseHistoryService(
            StudentCourseHistoryRepository studentCourseHistoryRepository) {
        this.studentCourseHistoryRepository = studentCourseHistoryRepository;
    }

    public List<CourseWithStatusProjection> getStudentCourseHistory(Integer studentId) {
        return studentCourseHistoryRepository.findCoursesByStudentId(studentId);
    }
}
