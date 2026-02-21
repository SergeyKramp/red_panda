import { FormEvent, useState } from "react";
import { useLoginMutation } from "features/authentication";
import styles from "./authentication-form.module.css";

export function AuthenticationForm() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const loginMutation = useLoginMutation();

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    try {
      const response = await loginMutation.mutateAsync({ username, password });

      if (!response.ok) {
        setError("Invalid username or password.");
        return;
      }
      setPassword("");
    } catch (loginError) {
      setError("Unable to reach the server.");
      console.error("Login failed", loginError);
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
            disabled={loginMutation.isPending}
            className={styles.primaryButton}
          >
            {loginMutation.isPending ? "Signing in..." : "Sign in"}
          </button>
        </form>
      </section>
    </main>
  );
}
