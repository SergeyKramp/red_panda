import styles from "./course-card.module.css";

export interface CourseCardProps {
  name: string;
  code: string;
  credits: number;
  specialization: string;
  onViewCourse?: () => void;
}

export function CourseCard({
  name,
  code,
  credits,
  specialization,
  onViewCourse,
}: CourseCardProps) {
  const viewCourseAriaLabel = `View Course: ${name} (${code})`;

  return (
    <article className={styles.card}>
      <header className={styles.header}>
        <h3 className={styles.title}>{name}</h3>
        <p className={styles.code}>{code}</p>
      </header>

      <footer className={styles.meta}>
        <p className={styles.metaItem}>
          <span className={styles.metaLabel}>Credits</span>
          <span className={styles.metaValue}>{credits}</span>
        </p>
        <p className={styles.metaItem}>
          <span className={styles.metaLabel}>Specialization</span>
          <span className={styles.metaValue}>{specialization}</span>
        </p>
      </footer>

      <button
        aria-label={viewCourseAriaLabel}
        className={styles.actionButton}
        onClick={onViewCourse}
        type="button"
      >
        View Course
      </button>
    </article>
  );
}
