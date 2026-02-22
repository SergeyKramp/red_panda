import { CourseCard } from "features/ui/course-card/course-card";
import { CourseInfo } from "features/api";
import styles from "./course-grid.module.css";

export interface CourseGridProps {
  courses: CourseInfo[];
  onViewCourse?: (course: CourseInfo) => void;
}

export function CourseGrid({ courses, onViewCourse }: CourseGridProps) {
  return (
    <section className={styles.gridViewport}>
      <div className={styles.grid}>
        {courses.map((course) => (
          <CourseCard
            code={course.code}
            credits={course.credits}
            key={course.code}
            name={course.name}
            onViewCourse={
              onViewCourse ? () => onViewCourse(course) : undefined
            }
            specialization={course.specialization}
          />
        ))}
      </div>
    </section>
  );
}
