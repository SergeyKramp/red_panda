package com.maplewood.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import com.maplewood.domain.Course;
import com.maplewood.domain.CourseHistoryStatus;
import com.maplewood.domain.CourseType;
import com.maplewood.domain.Semester;
import com.maplewood.domain.SemesterOrder;
import com.maplewood.domain.Specialization;
import com.maplewood.domain.Student;
import com.maplewood.domain.StudentCourseHistory;

@DataJpaTest
@ActiveProfiles("test")
class StudentCourseHistoryRepositoryTest {
    @Autowired
    private StudentCourseHistoryRepository studentCourseHistoryRepository;
    @Autowired
    private TestEntityManager entityManager;

    /**
     * Given: a student with passed and failed history entries, plus another student's passed entry
     * and duplicate passes for the same course across semesters
     *
     * When: findPassedCourseIdsByStudentId is called for that student
     *
     * Then: only distinct course ids from passed entries for that student should be returned
     */
    @Test
    void findPassedCourseIdsByStudentIdReturnsOnlyDistinctPassedCoursesForTargetStudent() {
        var specialization = persistSpecialization("Science");
        var targetStudent = persistStudent("target@student.test");
        var otherStudent = persistStudent("other@student.test");

        var biologyCourse = persistCourse("BIO101", "Biology I", specialization);
        var chemistryCourse = persistCourse("CHE101", "Chemistry I", specialization);
        var physicsCourse = persistCourse("PHY101", "Physics I", specialization);

        var fall2025 = persistSemester("Fall", 2025, SemesterOrder.FALL);
        var spring2026 = persistSemester("Spring", 2026, SemesterOrder.SPRING);

        persistHistory(targetStudent, biologyCourse, fall2025, CourseHistoryStatus.PASSED);
        persistHistory(targetStudent, biologyCourse, spring2026, CourseHistoryStatus.PASSED);
        persistHistory(targetStudent, chemistryCourse, spring2026, CourseHistoryStatus.FAILED);
        persistHistory(otherStudent, physicsCourse, fall2025, CourseHistoryStatus.PASSED);

        entityManager.flush();
        entityManager.clear();

        var passedCourseIds = studentCourseHistoryRepository
                .findPassedCourseIdsByStudentId(targetStudent.getId());

        assertThat(passedCourseIds).isEqualTo(Set.of(biologyCourse.getId()));
    }

    /**
     * Given: a student with no passed entries in history
     *
     * When: findPassedCourseIdsByStudentId is called for that student
     *
     * Then: an empty set should be returned
     */
    @Test
    void findPassedCourseIdsByStudentIdReturnsEmptySetWhenNoPassedHistoryExists() {
        var specialization = persistSpecialization("Math");
        var student = persistStudent("no-pass@student.test");
        var algebraCourse = persistCourse("MAT101", "Algebra I", specialization);
        var fall2025 = persistSemester("Fall", 2025, SemesterOrder.FALL);

        persistHistory(student, algebraCourse, fall2025, CourseHistoryStatus.FAILED);

        entityManager.flush();
        entityManager.clear();

        var passedCourseIds =
                studentCourseHistoryRepository.findPassedCourseIdsByStudentId(student.getId());

        assertThat(passedCourseIds).isEmpty();
    }

    /**
     * Given: a student with passed and failed records, repeated pass records for the same course,
     * and another student's passed record
     *
     * When: findEarnedCreditsByStudentId is called for the target student
     *
     * Then: earned credits should sum only distinct passed courses for that student
     */
    @Test
    void findEarnedCreditsByStudentIdSumsOnlyDistinctPassedCoursesForTargetStudent() {
        var specialization = persistSpecialization("Science");
        var targetStudent = persistStudent("credits-target@student.test");
        var otherStudent = persistStudent("credits-other@student.test");

        var biology = persistCourse("BIO101", "Biology I", specialization, 3.0);
        var chemistry = persistCourse("CHE101", "Chemistry I", specialization, 2.0);
        var physics = persistCourse("PHY101", "Physics I", specialization, 4.0);

        var fall2025 = persistSemester("Fall", 2025, SemesterOrder.FALL);
        var spring2026 = persistSemester("Spring", 2026, SemesterOrder.SPRING);

        persistHistory(targetStudent, biology, fall2025, CourseHistoryStatus.PASSED);
        persistHistory(targetStudent, biology, spring2026, CourseHistoryStatus.PASSED);
        persistHistory(targetStudent, chemistry, spring2026, CourseHistoryStatus.FAILED);
        persistHistory(otherStudent, physics, fall2025, CourseHistoryStatus.PASSED);

        entityManager.flush();
        entityManager.clear();

        var earnedCredits = studentCourseHistoryRepository
                .findEarnedCreditsByStudentId(targetStudent.getId());

        assertThat(earnedCredits).isEqualTo(3.0);
    }

    /**
     * Given: a student with no passed courses
     *
     * When: findEarnedCreditsByStudentId is called
     *
     * Then: zero credits should be returned
     */
    @Test
    void findEarnedCreditsByStudentIdReturnsZeroWhenNoPassedCoursesExist() {
        var specialization = persistSpecialization("Math");
        var student = persistStudent("credits-none@student.test");

        var algebra = persistCourse("MAT101", "Algebra I", specialization, 3.0);

        var fall2025 = persistSemester("Fall", 2025, SemesterOrder.FALL);
        persistHistory(student, algebra, fall2025, CourseHistoryStatus.FAILED);

        entityManager.flush();
        entityManager.clear();

        var earnedCredits = studentCourseHistoryRepository
                .findEarnedCreditsByStudentId(student.getId());

        assertThat(earnedCredits).isZero();
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
        return persistCourse(code, name, specialization, 3.0);
    }

    private Course persistCourse(String code, String name, Specialization specialization,
            Double credits) {
        var course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setCredits(credits);
        course.setHoursPerWeek(3);
        course.setCourseType(CourseType.CORE);
        course.setSemesterOrder(SemesterOrder.FALL);
        course.setGradeLevelMin(9);
        course.setGradeLevelMax(12);
        course.setSpecialization(specialization);
        return entityManager.persistFlushFind(course);
    }

    private Semester persistSemester(String name, Integer year, SemesterOrder order) {
        var semester = new Semester();
        semester.setName(name);
        semester.setYear(year);
        semester.setOrderInYear(order);
        semester.setActive(false);
        return entityManager.persistFlushFind(semester);
    }

    private void persistHistory(Student student, Course course, Semester semester,
            CourseHistoryStatus status) {
        var history = new StudentCourseHistory();
        history.setStudent(student);
        history.setCourse(course);
        history.setSemester(semester);
        history.setStatus(status);
        entityManager.persist(history);
    }
}
