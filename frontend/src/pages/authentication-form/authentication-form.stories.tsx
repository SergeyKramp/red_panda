import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ComponentType, useEffect } from "react";
import { AuthenticationForm } from "./authentication-form";

type FetchMockMode = "none" | "invalid-credentials";

type StoryStateProps = {
  prefill?: boolean;
  mockMode?: FetchMockMode;
  autoSubmit?: boolean;
};

function setInputValue(input: HTMLInputElement, value: string) {
  const valueSetter = Object.getOwnPropertyDescriptor(
    HTMLInputElement.prototype,
    "value",
  )?.set;

  valueSetter?.call(input, value);
  input.dispatchEvent(new Event("input", { bubbles: true }));
}

function StoryState({
  prefill = false,
  mockMode = "none",
  autoSubmit = false,
}: StoryStateProps) {
  useEffect(() => {
    if (!prefill) {
      return;
    }

    const usernameInput = document.getElementById("username") as
      | HTMLInputElement
      | null;
    const passwordInput = document.getElementById("password") as
      | HTMLInputElement
      | null;

    if (usernameInput) {
      setInputValue(usernameInput, "student");
    }

    if (passwordInput) {
      setInputValue(passwordInput, "password123");
    }
  }, [prefill]);

  useEffect(() => {
    if (mockMode === "none") {
      return;
    }

    const originalFetch = window.fetch;

    window.fetch = async () => {
      if (mockMode === "invalid-credentials") {
        return new Response(null, { status: 401 });
      }

      return new Response(null, { status: 500 });
    };

    return () => {
      window.fetch = originalFetch;
    };
  }, [mockMode]);

  useEffect(() => {
    if (!autoSubmit) {
      return;
    }

    const submitButton = document.querySelector(
      'button[type="submit"]',
    ) as HTMLButtonElement | null;

    if (!submitButton) {
      return;
    }

    const timeoutId = window.setTimeout(() => {
      submitButton.click();
    }, 0);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [autoSubmit]);

  return <AuthenticationForm />;
}

const meta = {
  title: "Pages/AuthenticationForm",
  component: AuthenticationForm,
  decorators: [
    (Story: ComponentType) => {
      const queryClient = new QueryClient({
        defaultOptions: {
          queries: { retry: false },
          mutations: { retry: false },
        },
      });

      return (
        <QueryClientProvider client={queryClient}>
          <Story />
        </QueryClientProvider>
      );
    },
  ],
};

export default meta;

export const Default = {
  render: () => <StoryState />,
};

export const Prefilled = {
  render: () => <StoryState prefill />,
};

export const InvalidCredentialsError = {
  render: () => (
    <StoryState
      prefill
      mockMode="invalid-credentials"
      autoSubmit
    />
  ),
};
