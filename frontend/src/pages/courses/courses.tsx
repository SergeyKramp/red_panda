import { CourseInfo } from "features/api";
import {
  useCoursesQuery,
  useSemesterCoursesQuery,
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
}

export function Courses({
  courses: providedCourses,
  semesterCourses: providedSemesterCourses,
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

  const allCourses = providedCourses ?? queriedCourses ?? [];
  const semesterCourses = providedSemesterCourses ?? queriedSemesterCourses ?? [];
  const activeCourses =
    activeFilter === "this-semester" ? semesterCourses : allCourses;

  const filteredCourses = useMemo(() => {
    if (activeFilter === "available-for-you") {
      return activeCourses.filter((course) => course.availableForYou);
    }

    return activeCourses;
  }, [activeCourses, activeFilter]);

  const isUsingProvidedData =
    (activeFilter === "this-semester" && !!providedSemesterCourses) ||
    (activeFilter !== "this-semester" && !!providedCourses);
  const isPending =
    activeFilter === "this-semester" ? isPendingSemesterCourses : isPendingCourses;
  const isError =
    activeFilter === "this-semester" ? isErrorSemesterCourses : isErrorCourses;

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

      {!isPending && !isError ? <CourseGrid courses={filteredCourses} /> : null}
    </section>
  );
}
