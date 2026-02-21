import { useAuthenticationStatusQuery } from "features/authentication";
import { AuthenticationForm } from "pages";
import { Dashboard } from "pages/dashboard/dashboard";

function App() {
  const { data: authenticated, isPending } = useAuthenticationStatusQuery();

  // Prevent rendering the app until we know the authentication status to avoid flashes of the login form.
  if (isPending) {
    return null;
  }

  if (!authenticated) {
    return <AuthenticationForm />;
  }

  return <Dashboard />;
}

export default App;
