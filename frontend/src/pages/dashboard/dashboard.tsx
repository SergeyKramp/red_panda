import { useCourseHistoryQuery } from "features/course-history";
import { CourseHistoryTable } from "features/ui";
import styles from "./dashboard.module.css";

export function Dashboard() {
  const {
    data: courseHistory,
    isPending,
    isError,
  } = useCourseHistoryQuery();

  const hasCourseHistory = (courseHistory?.length ?? 0) > 0;

  return (
    <section className={styles.pagePanel}>
      <header className={styles.header}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.subtitle}>Track your completed courses and credits.</p>
      </header>

      {isPending ? <p className={styles.stateText}>Loading course history...</p> : null}

      {isError ? <p className={styles.stateText}>Failed to load course history.</p> : null}

      {!isPending && !isError && !hasCourseHistory ? (
        <p className={styles.stateText}>No course history available yet.</p>
      ) : null}

      {!isPending && !isError && hasCourseHistory ? (
        <CourseHistoryTable courseHistory={courseHistory ?? []} />
      ) : null}
    </section>
  );
}
