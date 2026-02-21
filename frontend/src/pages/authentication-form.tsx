import { FormEvent, useState } from "react";
import { authenticate, login } from "features/authentication";
import styles from "./authentication-form.module.css";

export function AuthenticationForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);

    try {
      const response = await login({ username, password });

      if (!response.ok) {
        setError("Invalid username or password.");
        return;
      }
      setPassword("");

      // Check with the backend if the login was successful. This will also update the global authentication state
      const success = await authenticate();
      if (!success) {
        setError("Invalid username or password.");
      }
    } catch (loginError) {
      setError("Unable to reach the server.");
      console.error("Login failed", loginError);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className={styles.pageBackground}>
      <section className={styles.card}>
        <h1 className={styles.headingPrimary}>Sign in</h1>
        <p className={styles.subtitle}>
          Use your school account to access the overview.
        </p>
        <form className={styles.loginForm} onSubmit={onSubmit}>
          <label htmlFor="username" className={styles.fieldLabel}>
            Username
          </label>
          <input
            id="username"
            type="text"
            autoComplete="username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            className={styles.fieldInput}
            required
          />

          <label htmlFor="password" className={styles.fieldLabel}>
            Password
          </label>
          <input
            id="password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            className={styles.fieldInput}
            required
          />

          {error ? <p className={styles.error}>{error}</p> : null}
          <button
            type="submit"
            disabled={submitting}
            className={styles.primaryButton}
          >
            {submitting ? "Signing in..." : "Sign in"}
          </button>
        </form>
      </section>
    </main>
  );
}
