import { useState } from "react";
import { isLoggedIn } from "./features/authentication";
import { AuthenticationForm } from "./pages/authentication-form";
import styles from "./app.module.css";

function App() {
  const [authenticated, setAuthenticated] = useState(() => isLoggedIn());

  if (!authenticated) {
    return (
      <AuthenticationForm
        onLoginSuccess={async () => setAuthenticated(true)}
      />
    );
  }

  return (
    <main className={styles.appShell}>
      <section className={styles.card}>
        <h1 className={styles.headingPrimary}>Dashboard</h1>
        <p className={styles.subtitle}>You are logged in.</p>
      </section>
    </main>
  );
}

export default App;
