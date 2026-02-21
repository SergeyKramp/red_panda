import { HttpResponse, http } from "msw";
import { describe, expect, test, vi, beforeEach } from "vitest";
import { authenticate } from "./authentication";
import { useAuthenticationStore } from "./authentication-store";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";

const AUTHENTICATION_ME_ENDPOINT = `${API_BASE_URL}/api/auth/me`;

describe("authenticate", () => {
  beforeEach(() => {
    useAuthenticationStore.setState({ authenticated: null });
  });

  test("sets authenticated true when /me returns authenticated true", async () => {
    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () =>
        HttpResponse.json({ authenticated: true }),
      ),
    );

    const authenticated = await authenticate();

    expect(authenticated).toBe(true);
    expect(useAuthenticationStore.getState().authenticated).toBe(true);
  });

  test("sets authenticated false when /me returns authenticated false", async () => {
    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () =>
        HttpResponse.json({ authenticated: false }),
      ),
    );

    const authenticated = await authenticate();

    expect(authenticated).toBe(false);
    expect(useAuthenticationStore.getState().authenticated).toBe(false);
  });

  test("sets authenticated false when /me returns non-ok response", async () => {
    const consoleErrorSpy = vi
      .spyOn(console, "error")
      .mockImplementation(() => undefined);

    mockServer.use(
      http.get(
        AUTHENTICATION_ME_ENDPOINT,
        () => new HttpResponse(null, { status: 401 }),
      ),
    );

    const authenticated = await authenticate();

    expect(authenticated).toBe(false);
    expect(useAuthenticationStore.getState().authenticated).toBe(false);

    consoleErrorSpy.mockRestore();
  });

  test("sets authenticated false when /me payload is invalid", async () => {
    const consoleErrorSpy = vi
      .spyOn(console, "error")
      .mockImplementation(() => undefined);

    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () => HttpResponse.json({})),
    );

    const authenticated = await authenticate();

    expect(authenticated).toBe(false);
    expect(useAuthenticationStore.getState().authenticated).toBe(false);

    consoleErrorSpy.mockRestore();
  });
});
