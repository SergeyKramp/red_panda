import { HttpResponse, http } from "msw";
import { describe, expect, test, vi, beforeEach } from "vitest";
import { getAuthenticationStatus } from "./authentication";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";

const AUTHENTICATION_ME_ENDPOINT = `${API_BASE_URL}/api/auth/me`;

describe("getAuthenticationStatus", () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  test("returns true when /me returns authenticated true", async () => {
    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () =>
        HttpResponse.json({ authenticated: true }),
      ),
    );

    const authenticated = await getAuthenticationStatus();

    expect(authenticated).toBe(true);
  });

  test("returns false when /me returns authenticated false", async () => {
    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () =>
        HttpResponse.json({ authenticated: false }),
      ),
    );

    const authenticated = await getAuthenticationStatus();

    expect(authenticated).toBe(false);
  });

  test("returns false when /me returns non-ok response", async () => {
    const consoleErrorSpy = vi
      .spyOn(console, "error")
      .mockImplementation(() => undefined);

    mockServer.use(
      http.get(
        AUTHENTICATION_ME_ENDPOINT,
        () => new HttpResponse(null, { status: 401 }),
      ),
    );

    const authenticated = await getAuthenticationStatus();

    expect(authenticated).toBe(false);
    expect(consoleErrorSpy).toHaveBeenCalledTimes(1);
  });

  test("returns false when /me payload is invalid", async () => {
    const consoleErrorSpy = vi
      .spyOn(console, "error")
      .mockImplementation(() => undefined);

    mockServer.use(
      http.get(AUTHENTICATION_ME_ENDPOINT, () => HttpResponse.json({})),
    );

    const authenticated = await getAuthenticationStatus();

    expect(authenticated).toBe(false);
    expect(consoleErrorSpy).toHaveBeenCalledTimes(1);
  });
});
