package com.maplewood.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.maplewood.domain.Course;
import com.maplewood.repositories.StudentEnrollmentRepository;

@Service
public class StudentEnrollmentService {
    private final StudentEnrollmentRepository studentEnrollmentRepository;

    public StudentEnrollmentService(StudentEnrollmentRepository studentEnrollmentRepository) {
        this.studentEnrollmentRepository = studentEnrollmentRepository;
    }

    public List<Course> getActiveSemesterEnrollments(Integer studentId) {
        return studentEnrollmentRepository.findEnrolledCoursesByStudentIdInActiveSemester(studentId);
    }
}
