import { HttpResponse, http } from "msw";
import { describe, expect, test } from "vitest";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";
import { getStudentInfo } from "./student-info";

const STUDENT_INFO_ENDPOINT = `${API_BASE_URL}/api/dashboard/student/info`;

describe("getStudentInfo", () => {
  test("returns student info when API payload matches backend DTO", async () => {
    mockServer.use(
      http.get(STUDENT_INFO_ENDPOINT, () =>
        HttpResponse.json({
          firstName: "Emma",
          lastName: "Wilson",
          email: "emma.wilson@maplewood.edu",
          gradeLevel: 10,
          status: "ACTIVE",
          earnedCredits: 18.0,
        }),
      ),
    );

    const studentInfo = await getStudentInfo();

    expect(studentInfo).toEqual({
      firstName: "Emma",
      lastName: "Wilson",
      email: "emma.wilson@maplewood.edu",
      gradeLevel: 10,
      status: "ACTIVE",
      earnedCredits: 18.0,
    });
  });

  test("throws when API returns non-ok status", async () => {
    mockServer.use(
      http.get(STUDENT_INFO_ENDPOINT, () =>
        new HttpResponse(null, { status: 401 }),
      ),
    );

    await expect(getStudentInfo()).rejects.toThrow(
      "Failed to fetch student info: 401",
    );
  });

  test("throws when API payload does not match backend DTO", async () => {
    mockServer.use(
      http.get(STUDENT_INFO_ENDPOINT, () =>
        HttpResponse.json({
          firstName: "Emma",
        }),
      ),
    );

    await expect(getStudentInfo()).rejects.toThrow();
  });
});
