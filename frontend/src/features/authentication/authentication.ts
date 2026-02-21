import { API_BASE_URL } from "../api";
import z from "zod";

const LoginRequestZod = z.object({
  username: z.string(),
  password: z.string(),
});

export type LoginRequest = z.infer<typeof LoginRequestZod>;

const AUTHENTICATION_ENDPOINT = `${API_BASE_URL}/api/auth/login`;
const AUTHENTICATION_ME_ENDPOINT = `${API_BASE_URL}/api/auth/me`;

const AuthStatusResponseZod = z.object({
  authenticated: z.boolean(),
});

export async function login({
  username,
  password,
}: LoginRequest): Promise<Response> {
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

export async function getAuthenticationStatus(): Promise<boolean> {
  try {
    const response = await fetch(AUTHENTICATION_ME_ENDPOINT, {
      method: "GET",
      credentials: "include",
    });

    if (!response.ok) {
      console.error("Authentication check failed with status", response.status);
      return false;
    }

    const data = AuthStatusResponseZod.parse(await response.json());
    return data.authenticated;
  } catch (authCheckError) {
    console.error("Authentication check failed", authCheckError);
    return false;
  }
}
