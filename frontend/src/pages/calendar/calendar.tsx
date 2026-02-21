import styles from "./calendar.module.css";

export function Calendar() {
  return (
    <section className={styles.pagePanel}>
      <header>
        <h1>Calendar</h1>
      </header>
      <p>Plan your schedule and timeline here.</p>
    </section>
  );
}
