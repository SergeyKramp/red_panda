import { CourseCardInfo } from "features/api";
import {
  CourseFilter,
  CourseFilters,
  CourseGrid,
} from "features/ui";
import { useMemo, useState } from "react";
import styles from "./courses.module.css";

const COURSE_CATALOG: CourseCardInfo[] = [
  {
    code: "BIO-101",
    name: "Introduction to Biology",
    credits: 1,
    specialization: "Science",
    availableThisSemester: true,
    availableForYou: true,
  },
  {
    code: "CHEM-201",
    name: "Chemistry Foundations",
    credits: 1,
    specialization: "Science",
    availableThisSemester: true,
    availableForYou: false,
  },
  {
    code: "ALG-102",
    name: "Algebra II",
    credits: 1,
    specialization: "Mathematics",
    availableThisSemester: false,
    availableForYou: true,
  },
  {
    code: "LIT-115",
    name: "World Literature",
    credits: 1,
    specialization: "Humanities",
    availableThisSemester: true,
    availableForYou: true,
  },
  {
    code: "CS-120",
    name: "Computer Science Principles",
    credits: 1,
    specialization: "Technology",
    availableThisSemester: false,
    availableForYou: false,
  },
  {
    code: "ART-130",
    name: "Studio Art",
    credits: 0,
    specialization: "Arts",
    availableThisSemester: true,
    availableForYou: true,
  },
  {
    code: "HIST-210",
    name: "U.S. History",
    credits: 1,
    specialization: "Humanities",
    availableThisSemester: false,
    availableForYou: true,
  },
  {
    code: "ENV-205",
    name: "Environmental Systems",
    credits: 1,
    specialization: "Science",
    availableThisSemester: true,
    availableForYou: false,
  },
];

export function Courses() {
  const [activeFilter, setActiveFilter] = useState<CourseFilter>("all");

  const filteredCourses = useMemo(() => {
    if (activeFilter === "this-semester") {
      return COURSE_CATALOG.filter((course) => course.availableThisSemester);
    }

    if (activeFilter === "available-for-you") {
      return COURSE_CATALOG.filter((course) => course.availableForYou);
    }

    return COURSE_CATALOG;
  }, [activeFilter]);

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

      <CourseGrid courses={filteredCourses} />
    </section>
  );
}
