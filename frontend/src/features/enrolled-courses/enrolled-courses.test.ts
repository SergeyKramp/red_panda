import { HttpResponse, http } from "msw";
import { describe, expect, test } from "vitest";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";
import { getEnrolledCourses } from "./enrolled-courses";

const ENROLLED_COURSES_ENDPOINT =
  `${API_BASE_URL}/api/dashboard/student/enrolled-courses`;

describe("getEnrolledCourses", () => {
  test("returns enrolled courses when API payload matches backend DTO", async () => {
    mockServer.use(
      http.get(ENROLLED_COURSES_ENDPOINT, () =>
        HttpResponse.json({
          enrolledCourses: [
            {
              courseName: "English Composition",
              credits: "3.0",
            },
            {
              courseName: "Biology I",
              credits: "2.0",
            },
          ],
        }),
      ),
    );

    const enrolledCourses = await getEnrolledCourses();

    expect(enrolledCourses).toEqual([
      {
        courseName: "English Composition",
        credits: "3.0",
      },
      {
        courseName: "Biology I",
        credits: "2.0",
      },
    ]);
  });

  test("throws when API returns non-ok status", async () => {
    mockServer.use(
      http.get(ENROLLED_COURSES_ENDPOINT, () =>
        new HttpResponse(null, { status: 401 }),
      ),
    );

    await expect(getEnrolledCourses()).rejects.toThrow(
      "Failed to fetch enrolled courses: 401",
    );
  });

  test("throws when API payload does not match backend DTO", async () => {
    mockServer.use(
      http.get(ENROLLED_COURSES_ENDPOINT, () => HttpResponse.json({ rows: [] })),
    );

    await expect(getEnrolledCourses()).rejects.toThrow();
  });
});
