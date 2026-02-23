import { useCourseHistoryQuery } from "features/course-history";
import { useEnrolledCoursesQuery } from "features/enrolled-courses";
import { CourseHistoryTable, EnrollmentsTable } from "features/ui";
import styles from "./dashboard.module.css";

export function Dashboard() {
  const {
    data: courseHistory,
    isPending: isPendingCourseHistory,
    isError: isErrorCourseHistory,
  } = useCourseHistoryQuery();
  const {
    data: enrolledCourses,
    isPending: isPendingEnrolledCourses,
    isError: isErrorEnrolledCourses,
  } = useEnrolledCoursesQuery();

  const hasCourseHistory = (courseHistory?.length ?? 0) > 0;
  const hasEnrolledCourses = (enrolledCourses?.length ?? 0) > 0;

  return (
    <section className={styles.pagePanel}>
      <header className={styles.header}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.subtitle}>Track your completed courses and credits.</p>
      </header>

      {isPendingEnrolledCourses ? (
        <p className={styles.stateText}>Loading enrolled courses...</p>
      ) : null}

      {isErrorEnrolledCourses ? (
        <p className={styles.stateText}>Failed to load enrolled courses.</p>
      ) : null}

      {!isPendingEnrolledCourses && !isErrorEnrolledCourses && !hasEnrolledCourses ? (
        <p className={styles.stateText}>No active enrollments yet.</p>
      ) : null}

      {!isPendingEnrolledCourses && !isErrorEnrolledCourses && hasEnrolledCourses ? (
        <EnrollmentsTable enrolledCourses={enrolledCourses ?? []} />
      ) : null}

      {isPendingCourseHistory ? (
        <p className={styles.stateText}>Loading course history...</p>
      ) : null}

      {isErrorCourseHistory ? (
        <p className={styles.stateText}>Failed to load course history.</p>
      ) : null}

      {!isPendingCourseHistory && !isErrorCourseHistory && hasCourseHistory ? (
        <CourseHistoryTable courseHistory={courseHistory ?? []} />
      ) : null}
    </section>
  );
}
