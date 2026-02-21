import {
  authenticate,
  useAuthenticationStore,
} from "./features/authentication";
import { AuthenticationForm } from "./pages/authentication-form";
import styles from "./app.module.css";
import { useEffect } from "react";

function App() {
  const authenticated = useAuthenticationStore((state) => state.authenticated);

  useEffect(() => {
    if (authenticated === null) {
      void authenticate();
    }
  }, [authenticated]);

  // Prevent rendering the app until we know the authentication status to avoid flashes of the login form.
  if (authenticated === null) {
    return null;
  }

  if (!authenticated) {
    return <AuthenticationForm />;
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
