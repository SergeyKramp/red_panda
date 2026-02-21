import "./global.module.css";
import { useAuthenticationStatusQuery } from "features/authentication";
import { Layout } from "features/ui/layout/layout";
import { AuthenticationForm } from "pages";
import { Calendar } from "pages/calendar/calendar";
import { Courses } from "pages/courses/courses";
import { Dashboard } from "pages/dashboard/dashboard";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

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
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<Navigate replace to="/dashboard" />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/courses" element={<Courses />} />
          <Route path="/calendar" element={<Calendar />} />
          <Route path="*" element={<Navigate replace to="/dashboard" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
