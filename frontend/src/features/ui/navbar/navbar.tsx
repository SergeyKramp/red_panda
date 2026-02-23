import { NavLink } from "react-router-dom";
import { useId, useState } from "react";
import styles from "./navbar.module.css";

export function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuId = useId();

  const navLinkClassName = ({ isActive }: { isActive: boolean }) =>
    `${styles.navLink} ${isActive ? styles.active : ""}`;

  return (
    <nav className={styles.sidebar} aria-label="Main navigation">
      <div className={styles.topRow}>
        <section className={styles.brandArea}>
          <p className={styles.brandEyebrow}>Course Planner</p>
          <h2 className={styles.brandTitle}>Maplewood High</h2>
        </section>

        <button
          className={styles.menuButton}
          type="button"
          aria-expanded={isMenuOpen}
          aria-controls={menuId}
          aria-label="Toggle navigation menu"
          onClick={() => setIsMenuOpen((currentValue) => !currentValue)}
        >
          <span className={styles.menuIcon} aria-hidden="true">
            <span className={styles.menuBar} />
            <span className={styles.menuBar} />
            <span className={styles.menuBar} />
          </span>
        </button>
      </div>

      <div
        className={`${styles.menuContent} ${isMenuOpen ? styles.menuOpen : ""}`}
        id={menuId}
      >
        <p className={styles.sectionLabel}>Navigation</p>
        <ul className={styles.navList}>
          <li>
            <NavLink
              className={navLinkClassName}
              onClick={() => setIsMenuOpen(false)}
              to="/dashboard"
            >
              Dashboard
            </NavLink>
          </li>
          <li>
            <NavLink
              className={navLinkClassName}
              onClick={() => setIsMenuOpen(false)}
              to="/courses"
            >
              Courses
            </NavLink>
          </li>
          <li>
            <NavLink
              className={navLinkClassName}
              onClick={() => setIsMenuOpen(false)}
              to="/calendar"
            >
              Calendar
            </NavLink>
          </li>
        </ul>
      </div>
    </nav>
  );
}
