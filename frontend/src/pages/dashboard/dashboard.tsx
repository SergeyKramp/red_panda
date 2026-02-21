import styles from "./dashboard.module.css";

export function Dashboard() {
  return (
    <div className={styles.dashboardShell}>
      <div className={styles.navbarContainer}>
        <nav>
          <a href="/dashboard">Dashboard</a>
        </nav>
      </div>
      <main>
        <h1>Dashboard</h1>
        <p>You are logged in.</p>
      </main>
    </div>
  );
}
