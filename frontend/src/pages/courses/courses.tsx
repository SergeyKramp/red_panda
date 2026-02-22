import { CourseInfo } from "features/api";
import {
  useCoursesQuery,
  useSemesterCoursesQuery,
  useStudentCoursesQuery,
} from "features/courses";
import {
  CourseFilter,
  CourseFilters,
  CourseGrid,
} from "features/ui";
import { useMemo, useState } from "react";
import styles from "./courses.module.css";

export interface CoursesProps {
  courses?: CourseInfo[];
  semesterCourses?: CourseInfo[];
  studentCourses?: CourseInfo[];
}

export function Courses({
  courses: providedCourses,
  semesterCourses: providedSemesterCourses,
  studentCourses: providedStudentCourses,
}: CoursesProps) {
  const [activeFilter, setActiveFilter] = useState<CourseFilter>("all");
  const {
    data: queriedCourses,
    isPending: isPendingCourses,
    isError: isErrorCourses,
  } = useCoursesQuery({
    enabled: !providedCourses,
  });
  const {
    data: queriedSemesterCourses,
    isPending: isPendingSemesterCourses,
    isError: isErrorSemesterCourses,
  } = useSemesterCoursesQuery({
    enabled: !providedSemesterCourses && activeFilter === "this-semester",
  });
  const {
    data: queriedStudentCourses,
    isPending: isPendingStudentCourses,
    isError: isErrorStudentCourses,
  } = useStudentCoursesQuery({
    enabled: !providedStudentCourses && activeFilter === "available-for-you",
  });

  const allCourses = providedCourses ?? queriedCourses ?? [];
  const semesterCourses = providedSemesterCourses ?? queriedSemesterCourses ?? [];
  const studentCourses = providedStudentCourses ?? queriedStudentCourses ?? [];
  const activeCourses = useMemo(() => {
    if (activeFilter === "this-semester") {
      return semesterCourses;
    }
    if (activeFilter === "available-for-you") {
      return studentCourses;
    }
    return allCourses;
  }, [activeFilter, allCourses, semesterCourses, studentCourses]);

  const isUsingProvidedData =
    (activeFilter === "this-semester" && !!providedSemesterCourses) ||
    (activeFilter === "available-for-you" && !!providedStudentCourses) ||
    (activeFilter === "all" && !!providedCourses);
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

      {!isUsingProvidedData && isPending ? (
        <p className={styles.stateText}>Loading courses...</p>
      ) : null}

      {!isUsingProvidedData && isError ? (
        <p className={styles.stateText}>Failed to load courses.</p>
      ) : null}

      {!isPending && !isError ? <CourseGrid courses={activeCourses} /> : null}
    </section>
  );
}
