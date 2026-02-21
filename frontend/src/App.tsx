import { useAuthenticationStatusQuery } from "features/authentication";
import { AuthenticationForm } from "pages";
import styles from "./app.module.css";

function App() {
  const { data: authenticated, isPending } = useAuthenticationStatusQuery();

  // Prevent rendering the app until we know the authentication status to avoid flashes of the login form.
  if (isPending) {
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
