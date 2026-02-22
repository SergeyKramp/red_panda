import { CourseCardInfo } from "features/api";
import { useCoursesQuery } from "features/courses";
import {
  CourseFilter,
  CourseFilters,
  CourseGrid,
} from "features/ui";
import { useMemo, useState } from "react";
import styles from "./courses.module.css";

export interface CoursesProps {
  courses?: CourseCardInfo[];
}

export function Courses({ courses: providedCourses }: CoursesProps) {
  const [activeFilter, setActiveFilter] = useState<CourseFilter>("all");
  const { data: queriedCourses, isPending, isError } = useCoursesQuery({
    enabled: !providedCourses,
  });

  const courses = providedCourses ?? queriedCourses ?? [];

  const filteredCourses = useMemo(() => {
    if (activeFilter === "this-semester") {
      return courses.filter((course) => course.availableThisSemester);
    }

    if (activeFilter === "available-for-you") {
      return courses.filter((course) => course.availableForYou);
    }

    return courses;
  }, [activeFilter, courses]);

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

      {!providedCourses && isPending ? (
        <p className={styles.stateText}>Loading courses...</p>
      ) : null}

      {!providedCourses && isError ? (
        <p className={styles.stateText}>Failed to load courses.</p>
      ) : null}

      {!isPending && !isError ? <CourseGrid courses={filteredCourses} /> : null}
    </section>
  );
}
