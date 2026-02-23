import { useCourseHistoryQuery } from "features/course-history";
import { useEnrolledCoursesQuery } from "features/enrolled-courses";
import { useStudentInfoQuery } from "features/student-info";
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
  const {
    data: studentInfo,
    isPending: isPendingStudentInfo,
    isError: isErrorStudentInfo,
  } = useStudentInfoQuery();
  const earnedCredits = studentInfo?.earnedCredits;
  const safeEarnedCredits = typeof earnedCredits === "number" ? earnedCredits : 0;
  const creditsProgressValue = Math.min(safeEarnedCredits, 30);
  const gradeLevelValue = studentInfo?.gradeLevel;
  const enrolledCourseCount = enrolledCourses?.length;

  return (
    <section className={styles.pagePanel}>
      <header className={styles.header}>
        <h1 className={styles.title}>Dashboard</h1>
        <p className={styles.subtitle}>Track your completed courses and credits.</p>
      </header>

      {isPendingStudentInfo ? (
        <p className={styles.stateText}>Loading student info...</p>
      ) : null}

      {isErrorStudentInfo ? (
        <p className={styles.stateText}>Failed to load student info.</p>
      ) : null}

      {!isPendingStudentInfo && !isErrorStudentInfo && studentInfo ? (
        <dl className={styles.studentInfoList}>
          <dt className={styles.studentInfoLabel}>First name</dt>
          <dd className={styles.studentInfoValue}>{studentInfo.firstName}</dd>
          <dt className={styles.studentInfoLabel}>Last name</dt>
          <dd className={styles.studentInfoValue}>{studentInfo.lastName}</dd>
          <dt className={styles.studentInfoLabel}>Email</dt>
          <dd className={styles.studentInfoValue}>{studentInfo.email}</dd>
          <dt className={styles.studentInfoLabel}>Status</dt>
          <dd className={styles.studentInfoValue}>{studentInfo.status ?? "Unknown"}</dd>
        </dl>
      ) : null}

      <section aria-label="dashboard metrics" className={styles.widgetsGrid}>
        <article className={styles.widget}>
          <h2 className={styles.widgetTitle}>Credits earned</h2>
          <p className={styles.widgetValue}>
            {isPendingStudentInfo ? "..." : `${safeEarnedCredits.toFixed(1)} / 30`}
          </p>
          <progress
            aria-label="Credits earned progress"
            aria-valuetext={`${safeEarnedCredits.toFixed(1)} out of 30 credits`}
            className={styles.widgetProgress}
            max={30}
            value={isPendingStudentInfo ? 0 : creditsProgressValue}
          />
        </article>

        <article className={styles.widget}>
          <h2 className={styles.widgetTitle}>Grade level</h2>
          <p className={styles.widgetValue}>
            {isPendingStudentInfo ? "..." : gradeLevelValue ?? "Unknown"}
          </p>
        </article>

        <article className={styles.widget}>
          <h2 className={styles.widgetTitle}>Courses this semester</h2>
          <p className={styles.widgetValue}>
            {isPendingEnrolledCourses ? "..." : `${enrolledCourseCount ?? 0} / 5`}
          </p>
        </article>
      </section>

      {isPendingEnrolledCourses ? (
        <p className={styles.stateText}>Loading enrolled courses...</p>
      ) : null}

      {isErrorEnrolledCourses ? (
        <p className={styles.stateText}>Failed to load enrolled courses.</p>
      ) : null}

      {!isPendingEnrolledCourses && !isErrorEnrolledCourses ? (
        <EnrollmentsTable enrolledCourses={enrolledCourses ?? []} />
      ) : null}

      {isPendingCourseHistory ? (
        <p className={styles.stateText}>Loading course history...</p>
      ) : null}

      {isErrorCourseHistory ? (
        <p className={styles.stateText}>Failed to load course history.</p>
      ) : null}

      {!isPendingCourseHistory && !isErrorCourseHistory ? (
        <CourseHistoryTable courseHistory={courseHistory ?? []} />
      ) : null}
    </section>
  );
}
