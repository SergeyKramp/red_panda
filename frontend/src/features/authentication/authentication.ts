import { API_BASE_URL } from "../api";
import z from "zod";

const LoginRequestZod = z.object({
  username: z.string(),
  password: z.string(),
});

export type LoginRequest = z.infer<typeof LoginRequestZod>;

const AUTHENTICATION_ENDPOINT = `${API_BASE_URL}/api/auth/login`;

export async function login({ username, password }: LoginRequest) {
  const body = new URLSearchParams({
    username,
    password,
  });

  return fetch(AUTHENTICATION_ENDPOINT, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body,
  });
}

/**
 * Check if authtentication cookie is present.
 */
export function isLoggedIn() {
  if (typeof document === "undefined") {
    return false;
  }

  return document.cookie
    .split(";")
    .map((cookie) => cookie.trim())
    .some((cookie) => cookie.startsWith("JSESSIONID="));
}
