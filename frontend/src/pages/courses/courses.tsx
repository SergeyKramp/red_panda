import styles from "./courses.module.css";

export function Courses() {
  return (
    <section className={styles.pagePanel}>
      <header>
        <h1>Courses</h1>
      </header>
      <p>Browse your available courses here.</p>
    </section>
  );
}
