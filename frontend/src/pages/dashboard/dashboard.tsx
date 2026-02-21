import styles from "./dashboard.module.css";

export function Dashboard() {
  return (
    <section className={styles.pagePanel}>
      <header>
        <h1>Dashboard</h1>
      </header>
      <p>You are logged in.</p>
    </section>
  );
}
