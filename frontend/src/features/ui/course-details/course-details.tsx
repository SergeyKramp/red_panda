import { CourseInfo } from "features/api";
import {
  getEnrollmentFailureMessage,
  useEnrollInCourseMutation,
} from "features/enrollment";
import { useEffect, useState } from "react";
import styles from "./course-details.module.css";

export interface CourseDetailsProps {
  course: CourseInfo;
}

export function CourseDetails({
  course,
}: CourseDetailsProps) {
  const [enrollmentMessage, setEnrollmentMessage] = useState<string | null>(null);
  const [enrollmentMessageTone, setEnrollmentMessageTone] = useState<"error" | "success">("error");
  const { isPending, mutateAsync, reset } = useEnrollInCourseMutation();

  useEffect(() => {
    setEnrollmentMessage(null);
    setEnrollmentMessageTone("error");
    reset();
  }, [course.id, reset]);

  const handleSignUpCourse = async () => {
    setEnrollmentMessage(null);
    setEnrollmentMessageTone("error");

    try {
      const result = await mutateAsync(course.id);

      if (!result.ok) {
        setEnrollmentMessage(getEnrollmentFailureMessage(result.code));
        return;
      }

      setEnrollmentMessageTone("success");
      setEnrollmentMessage("Enrollment successful. You have successfully enrolled in this course.");
    } catch {
      setEnrollmentMessage("Enrollment failed. Please try again.");
    }
  };

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

      <div className={styles.enrollmentFooter}>
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
          aria-label={`Sign up for ${course.name}`}
          className={styles.signUpButton}
          disabled={isPending}
          onClick={handleSignUpCourse}
          type="button"
        >
          {isPending ? "Signing up..." : "Sign Up for Course"}
        </button>
      </div>
    </div>
  );
}
