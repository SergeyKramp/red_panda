package com.maplewood.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseType;
import com.maplewood.domain.Semester;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;
import com.maplewood.domain.Student;
import com.maplewood.domain.StudentEnrollment;
import com.maplewood.domain.StudentEnrollmentStatus;

@DataJpaTest
@ActiveProfiles("test")
class StudentEnrollmentRepositoryTest {
    @Autowired
    private StudentEnrollmentRepository studentEnrollmentRepository;
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Given: a student, a course, and an active semester
     *
     * When: addEnrollment is called with the student and course ids
     *
     * Then: one enrollment record should be inserted with status enrolled in the active semester
     */
    @Test
    void addEnrollmentInsertsEnrolledRowForActiveSemester() {
        var specialization = persistSpecialization("Science");
        var student = persistStudent("enroll-success@student.test");
        var course = persistCourse("SCI101", "General Science", specialization);
        var activeSemester = persistSemester("Fall", 2026, SemesterOrder.FALL, true);
        persistSemester("Spring", 2026, SemesterOrder.SPRING, false);

        var affectedRows = studentEnrollmentRepository.addEnrollment(student.getId(), course.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(affectedRows).isEqualTo(1);

        var insertedRows = entityManager.getEntityManager()
                .createNativeQuery("""
                        SELECT student_id, course_id, semester_id, status
                        FROM student_enrollments
                        """)
                .getResultList();

        assertThat(insertedRows).hasSize(1);

        var insertedRow = (Object[]) insertedRows.get(0);
        assertThat(((Number) insertedRow[0]).intValue()).isEqualTo(student.getId());
        assertThat(((Number) insertedRow[1]).intValue()).isEqualTo(course.getId());
        assertThat(((Number) insertedRow[2]).intValue()).isEqualTo(activeSemester.getId());
        assertThat(insertedRow[3]).isEqualTo(StudentEnrollmentStatus.ENROLLED.getValue());
    }

    /**
     * Given: a student and a course but no active semester
     *
     * When: addEnrollment is called
     *
     * Then: no enrollment should be inserted
     */
    @Test
    void addEnrollmentReturnsZeroWhenNoActiveSemesterExists() {
        var specialization = persistSpecialization("Math");
        var student = persistStudent("enroll-no-active@student.test");
        var course = persistCourse("MAT101", "Algebra I", specialization);
        persistSemester("Spring", 2027, SemesterOrder.SPRING, false);

        var affectedRows = studentEnrollmentRepository.addEnrollment(student.getId(), course.getId());
        entityManager.flush();
        entityManager.clear();

        assertThat(affectedRows).isZero();
        assertThat(studentEnrollmentRepository.findAll()).isEmpty();
    }

    /**
     * Given: a target student with enrollments across active and inactive semesters, mixed
     * statuses, and another student's active enrollment
     *
     * When: findEnrolledCoursesByStudentIdInActiveSemester is called for the target student
     *
     * Then: only the target student's enrolled courses from active semesters should be returned
     */
    @Test
    void findEnrolledCoursesByStudentIdInActiveSemesterReturnsOnlyActiveEnrolledCoursesForStudent() {
        var specialization = persistSpecialization("History");
        var targetStudent = persistStudent("enroll-target@student.test");
        var otherStudent = persistStudent("enroll-other@student.test");

        var worldHistory = persistCourse("HIS101", "World History", specialization);
        var civics = persistCourse("HIS102", "Civics", specialization);
        var geography = persistCourse("HIS103", "Geography", specialization);

        var activeSemester = persistSemester("Fall", 2028, SemesterOrder.FALL, true);
        var inactiveSemester = persistSemester("Spring", 2028, SemesterOrder.SPRING, false);

        persistEnrollment(targetStudent, worldHistory, activeSemester, StudentEnrollmentStatus.ENROLLED);
        persistEnrollment(targetStudent, civics, activeSemester, StudentEnrollmentStatus.DROPPED);
        persistEnrollment(targetStudent, geography, inactiveSemester, StudentEnrollmentStatus.ENROLLED);
        persistEnrollment(otherStudent, civics, activeSemester, StudentEnrollmentStatus.ENROLLED);

        entityManager.flush();
        entityManager.clear();

        var enrolledCourses = studentEnrollmentRepository
                .findEnrolledCoursesByStudentIdInActiveSemester(targetStudent.getId());

        assertThat(enrolledCourses).hasSize(1);
        assertThat(enrolledCourses.stream()
                .map(Course::getId)
                .sorted(Comparator.naturalOrder())
                .toList()).containsExactly(worldHistory.getId());
    }

    /**
     * Given: a student with no enrolled records in any active semester
     *
     * When: findEnrolledCoursesByStudentIdInActiveSemester is called
     *
     * Then: an empty list should be returned
     */
    @Test
    void findEnrolledCoursesByStudentIdInActiveSemesterReturnsEmptyWhenNoMatchingRecordsExist() {
        var specialization = persistSpecialization("Science");
        var student = persistStudent("enroll-empty@student.test");
        var biology = persistCourse("BIO101", "Biology I", specialization);
        var inactiveSemester = persistSemester("Spring", 2029, SemesterOrder.SPRING, false);

        persistEnrollment(student, biology, inactiveSemester, StudentEnrollmentStatus.ENROLLED);

        entityManager.flush();
        entityManager.clear();

        var enrolledCourses = studentEnrollmentRepository
                .findEnrolledCoursesByStudentIdInActiveSemester(student.getId());

        assertThat(enrolledCourses).isEmpty();
    }

    private Specialization persistSpecialization(String name) {
        var specialization = new Specialization();
        specialization.setName(name);
        return entityManager.persistFlushFind(specialization);
    }

    private Student persistStudent(String email) {
        var student = new Student();
        student.setFirstName("Test");
        student.setLastName("Student");
        student.setEmail(email);
        student.setGradeLevel(10);
        student.setEnrollmentYear(2024);
        student.setExpectedGraduationYear(2028);
        return entityManager.persistFlushFind(student);
    }

    private Course persistCourse(String code, String name, Specialization specialization) {
        var course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setCredits(3.0);
        course.setHoursPerWeek(3);
        course.setCourseType(CourseType.CORE);
        course.setSemesterOrder(SemesterOrder.FALL);
        course.setGradeLevelMin(9);
        course.setGradeLevelMax(12);
        course.setSpecialization(specialization);
        return entityManager.persistFlushFind(course);
    }

    private Semester persistSemester(String name, Integer year, SemesterOrder order, boolean active) {
        var semester = new Semester();
        semester.setName(name);
        semester.setYear(year);
        semester.setOrderInYear(order);
        semester.setActive(active);
        return entityManager.persistFlushFind(semester);
    }

    private void persistEnrollment(Student student, Course course, Semester semester,
            StudentEnrollmentStatus status) {
        var enrollment = new StudentEnrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(semester);
        enrollment.setStatus(status);
        entityManager.persist(enrollment);
    }
}
