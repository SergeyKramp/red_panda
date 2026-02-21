import styles from "./navbar.module.css";

export function Navbar() {
  return (
    <nav className={styles.sidebar}>
      <section className={styles.brandArea}>
        <p className={styles.brandEyebrow}>Course Planner</p>
        <h2 className={styles.brandTitle}>Red Panda</h2>
      </section>

      <p className={styles.sectionLabel}>Navigation</p>
      <ul className={styles.navList}>
        <li>
          <a className={`${styles.navLink} ${styles.active}`} href="/dashboard">
            Dashboard
          </a>
        </li>
        <li>
          <a className={styles.navLink} href="/courses">
            Courses
          </a>
        </li>
        <li>
          <a className={styles.navLink} href="/calendar">
            Calendar
          </a>
        </li>
      </ul>
    </nav>
  );
}
