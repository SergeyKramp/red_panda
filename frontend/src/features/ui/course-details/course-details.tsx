import { CourseInfo } from "features/api";
import styles from "./course-details.module.css";

export interface CourseDetailsProps {
  course: CourseInfo;
  onSignUpCourse?: (course: CourseInfo) => void;
  isEnrollmentPending?: boolean;
  enrollmentMessage?: string | null;
  enrollmentMessageTone?: "error" | "success";
}

export function CourseDetails({
  course,
  onSignUpCourse,
  isEnrollmentPending = false,
  enrollmentMessage = null,
  enrollmentMessageTone = "error",
}: CourseDetailsProps) {
  return (
    <div className={styles.courseDetails}>
      <p className={styles.courseDescription}>{course.description}</p>

      <dl className={styles.detailsList}>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Credits</dt>
          <dd className={styles.detailValue}>{course.credits}</dd>
        </div>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Hours / Week</dt>
          <dd className={styles.detailValue}>{course.hoursPerWeek}</dd>
        </div>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Specialization</dt>
          <dd className={styles.detailValue}>{course.specialization}</dd>
        </div>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Course Type</dt>
          <dd className={styles.detailValue}>{course.courseType}</dd>
        </div>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Grade Range</dt>
          <dd className={styles.detailValue}>
            {course.gradeLevelMin} - {course.gradeLevelMax}
          </dd>
        </div>
        <div className={styles.detailItem}>
          <dt className={styles.detailLabel}>Prerequisite</dt>
          <dd className={styles.detailValue}>{course.prerequisite ?? "None"}</dd>
        </div>
      </dl>

      {enrollmentMessage ? (
        <p
          aria-live="polite"
          className={`${styles.enrollmentMessage} ${enrollmentMessageTone === "success" ? styles.enrollmentMessageSuccess : styles.enrollmentMessageError}`}
          role="status"
        >
          {enrollmentMessage}
        </p>
      ) : null}

      <button
        className={styles.signUpButton}
        disabled={isEnrollmentPending}
        onClick={() => onSignUpCourse?.(course)}
        type="button"
      >
        {isEnrollmentPending ? "Signing up..." : "Sign Up for Course"}
      </button>
    </div>
  );
}
