import styles from "./navbar.module.css";
import { NavLink } from "react-router-dom";

export function Navbar() {
  return (
    <nav className={styles.sidebar}>
      <section className={styles.brandArea}>
        <p className={styles.brandEyebrow}>Course Planner</p>
        <h2 className={styles.brandTitle}>Maplewood High</h2>
      </section>

      <p className={styles.sectionLabel}>Navigation</p>
      <ul className={styles.navList}>
        <li>
          <NavLink
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.active : ""}`
            }
            to="/dashboard"
          >
            Dashboard
          </NavLink>
        </li>
        <li>
          <NavLink
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.active : ""}`
            }
            to="/courses"
          >
            Courses
          </NavLink>
        </li>
        <li>
          <NavLink
            className={({ isActive }) =>
              `${styles.navLink} ${isActive ? styles.active : ""}`
            }
            to="/calendar"
          >
            Calendar
          </NavLink>
        </li>
      </ul>
    </nav>
  );
}
