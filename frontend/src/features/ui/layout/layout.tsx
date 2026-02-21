import { Navbar } from "features/ui/navbar/navbar";
import { Outlet } from "react-router-dom";
import styles from "./layout.module.css";

export function Layout() {
  return (
    <div className={styles.dashboardShell}>
      <aside className={styles.navbarContainer}>
        <Navbar />
      </aside>
      <main className={styles.mainContent}>
        <Outlet />
      </main>
    </div>
  );
}
