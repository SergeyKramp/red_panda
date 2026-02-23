import { HttpResponse, http } from "msw";
import { beforeEach, describe, expect, test } from "vitest";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";
import { enrollInCourse } from "./enrollment";

const ENROLLMENT_ENDPOINT = `${API_BASE_URL}/api/courses/enroll/c`;

describe("enrollInCourse", () => {
  beforeEach(() => {
    document.cookie = "XSRF-TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
  });

  test("sends csrf header from XSRF-TOKEN cookie on enrollment request", async () => {
    document.cookie = "XSRF-TOKEN=test-csrf-token-value";
    let receivedCsrfHeader: string | null = null;

    mockServer.use(
      http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, ({ request }) => {
        receivedCsrfHeader = request.headers.get("X-XSRF-TOKEN");
        return new HttpResponse(null, { status: 200 });
      }),
    );

    await enrollInCourse(101);

    expect(receivedCsrfHeader).toBe("test-csrf-token-value");
  });

  test("returns without error when enrollment succeeds", async () => {
    mockServer.use(
      http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, () => new HttpResponse(null, { status: 204 })),
    );

    await expect(enrollInCourse(101)).resolves.toEqual({ ok: true });
  });

  test("returns enrollment conflict code for 409 responses", async () => {
    mockServer.use(
      http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, () =>
        HttpResponse.json(
          {
            messageCode: "COURSE_ALREADY_ENROLLED",
            courseId: 101,
          },
          { status: 409 },
        ),
      ),
    );

    await expect(enrollInCourse(101)).resolves.toEqual({
      ok: false,
      code: "COURSE_ALREADY_ENROLLED",
    });
  });

  test("throws standard error for non-409 failure responses", async () => {
    mockServer.use(
      http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, () => new HttpResponse(null, { status: 500 })),
    );

    await expect(enrollInCourse(101)).rejects.toThrow("Failed to enroll in course: 500");
  });
});
