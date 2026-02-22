import { CourseInfo } from "features/api";
import {
  useCoursesQuery,
  useSemesterCoursesQuery,
  useStudentCoursesQuery,
} from "features/courses";
import {
  describeEnrollmentFailure,
  EnrollmentConflictError,
  useEnrollInCourseMutation,
} from "features/enrollment";
import {
  CourseDetails,
  CourseFilter,
  CourseFilters,
  CourseGrid,
  Drawer,
} from "features/ui";
import { useMemo, useState } from "react";
import styles from "./courses.module.css";

export function Courses() {
  const [activeFilter, setActiveFilter] = useState<CourseFilter>("all");
  const [selectedCourse, setSelectedCourse] = useState<CourseInfo | null>(null);
  const [enrollmentMessage, setEnrollmentMessage] = useState<string | null>(null);
  const [enrollmentMessageTone, setEnrollmentMessageTone] = useState<"error" | "success">("error");
  const {
    data: queriedCourses,
    isPending: isPendingCourses,
    isError: isErrorCourses,
  } = useCoursesQuery();
  const {
    data: queriedSemesterCourses,
    isPending: isPendingSemesterCourses,
    isError: isErrorSemesterCourses,
  } = useSemesterCoursesQuery({
    enabled: activeFilter === "this-semester",
  });
  const {
    data: queriedStudentCourses,
    isPending: isPendingStudentCourses,
    isError: isErrorStudentCourses,
  } = useStudentCoursesQuery({
    enabled: activeFilter === "available-for-you",
  });

  const allCourses = queriedCourses ?? [];
  const semesterCourses = queriedSemesterCourses ?? [];
  const studentCourses = queriedStudentCourses ?? [];

  const activeCourses = useMemo(() => {
    if (activeFilter === "this-semester") {
      return semesterCourses;
    }
    if (activeFilter === "available-for-you") {
      return studentCourses;
    }
    return allCourses;
  }, [activeFilter, allCourses, semesterCourses, studentCourses]);

  const isPending = activeFilter === "this-semester"
    ? isPendingSemesterCourses
    : activeFilter === "available-for-you"
      ? isPendingStudentCourses
      : isPendingCourses;
  const isError = activeFilter === "this-semester"
    ? isErrorSemesterCourses
    : activeFilter === "available-for-you"
      ? isErrorStudentCourses
      : isErrorCourses;

  const enrollInCourseMutation = useEnrollInCourseMutation();

  const openCourseDetails = (course: CourseInfo) => {
    setEnrollmentMessage(null);
    enrollInCourseMutation.reset();
    setSelectedCourse(course);
  };

  const closeCourseDetails = () => {
    setEnrollmentMessage(null);
    enrollInCourseMutation.reset();
    setSelectedCourse(null);
  };

  const handleSignUpCourse = async (course: CourseInfo) => {
    setEnrollmentMessage(null);

    try {
      await enrollInCourseMutation.mutateAsync(course.id);
      setEnrollmentMessageTone("success");
      setEnrollmentMessage("Enrollment successful. Your available courses are being refreshed.");
    } catch (enrollmentError) {
      setEnrollmentMessageTone("error");

      if (enrollmentError instanceof EnrollmentConflictError) {
        const fallbackMessage = describeEnrollmentFailure(enrollmentError.code);
        setEnrollmentMessage(enrollmentError.message || fallbackMessage);
        return;
      }

      setEnrollmentMessage("Enrollment failed. Please try again.");
    }
  };

  return (
    <section className={styles.pagePanel}>
      <header className={styles.header}>
        <h1 className={styles.title}>Course Catalog</h1>
        <p className={styles.subtitle}>Browse and preview Maplewood courses.</p>
      </header>

      <CourseFilters
        activeFilter={activeFilter}
        onFilterChange={setActiveFilter}
      />

      {isPending ? (
        <p className={styles.stateText}>Loading courses...</p>
      ) : null}

      {isError ? (
        <p className={styles.stateText}>Failed to load courses.</p>
      ) : null}

      {!isPending && !isError ? (
        <CourseGrid
          courses={activeCourses}
          onViewCourse={openCourseDetails}
        />
      ) : null}

      <Drawer
        closeLabel="Close course details"
        isOpen={selectedCourse !== null}
        onClose={closeCourseDetails}
        subtitle={selectedCourse ? selectedCourse.code : undefined}
        title={selectedCourse ? selectedCourse.name : "Course details"}
      >
        {selectedCourse ? (
          <CourseDetails
            course={selectedCourse}
            enrollmentMessage={enrollmentMessage}
            enrollmentMessageTone={enrollmentMessageTone}
            isEnrollmentPending={enrollInCourseMutation.isPending}
            onSignUpCourse={handleSignUpCourse}
          />
        ) : null}
      </Drawer>
    </section>
  );
}
