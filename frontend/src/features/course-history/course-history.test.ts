import { HttpResponse, http } from "msw";
import { describe, expect, test } from "vitest";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";
import { getCourseHistory } from "./course-history";

const COURSE_HISTORY_ENDPOINT = `${API_BASE_URL}/api/dashboard/student/course-history`;

describe("getCourseHistory", () => {
  test("returns course history lines when API payload matches backend DTO", async () => {
    mockServer.use(
      http.get(COURSE_HISTORY_ENDPOINT, () =>
        HttpResponse.json({
          courseHistory: [
            {
              courseName: "English Composition",
              credits: "3.0",
              status: "PASSED",
            },
            {
              courseName: "World History",
              credits: "2.0",
              status: "FAILED",
            },
          ],
        }),
      ),
    );

    const courseHistory = await getCourseHistory();

    expect(courseHistory).toEqual([
      {
        courseName: "English Composition",
        credits: "3.0",
        status: "PASSED",
      },
      {
        courseName: "World History",
        credits: "2.0",
        status: "FAILED",
      },
    ]);
  });

  test("throws when API returns non-ok status", async () => {
    mockServer.use(
      http.get(COURSE_HISTORY_ENDPOINT, () =>
        new HttpResponse(null, { status: 401 }),
      ),
    );

    await expect(getCourseHistory()).rejects.toThrow(
      "Failed to fetch course history: 401",
    );
  });

  test("throws when API payload does not match backend DTO", async () => {
    mockServer.use(
      http.get(COURSE_HISTORY_ENDPOINT, () => HttpResponse.json({ lines: [] })),
    );

    await expect(getCourseHistory()).rejects.toThrow();
  });
});
