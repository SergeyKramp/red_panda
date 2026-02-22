import { HttpResponse, http } from "msw";
import { describe, expect, test } from "vitest";
import { API_BASE_URL } from "features/api";
import { mockServer } from "test-utils/mock-server";
import { getCourses, getSemesterCourses } from "./courses";

const COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/`;
const SEMESTER_COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/semester`;

describe("getCourses", () => {
  test("returns mapped courses when API payload matches backend DTO", async () => {
    mockServer.use(
      http.get(COURSES_ENDPOINT, () =>
        HttpResponse.json([
          {
            id: 1,
            code: "BIO-101",
            name: "Introduction to Biology",
            description: "Foundational biology topics.",
            credits: 1,
            hoursPerWeek: 5,
            specialization: "Science",
            prerequisite: null,
            courseType: "REGULAR",
            gradeLevelMin: 9,
            gradeLevelMax: 12,
          },
        ]),
      ),
    );

    const courses = await getCourses();

    expect(courses).toEqual([
      {
        id: 1,
        code: "BIO-101",
        name: "Introduction to Biology",
        description: "Foundational biology topics.",
        credits: 1,
        hoursPerWeek: 5,
        specialization: "Science",
        prerequisite: null,
        courseType: "REGULAR",
        gradeLevelMin: 9,
        gradeLevelMax: 12,
        availableForYou: false,
      },
    ]);
  });

  test("throws when API returns non-ok status", async () => {
    mockServer.use(
      http.get(COURSES_ENDPOINT, () => new HttpResponse(null, { status: 500 })),
    );

    await expect(getCourses()).rejects.toThrow("Failed to fetch courses: 500");
  });

  test("throws when API payload does not match backend DTO", async () => {
    mockServer.use(
      http.get(COURSES_ENDPOINT, () =>
        HttpResponse.json([
          {
            code: "BIO-101",
          },
        ]),
      ),
    );

    await expect(getCourses()).rejects.toThrow();
  });
});

describe("getSemesterCourses", () => {
  test("returns mapped courses when semester endpoint payload matches backend DTO", async () => {
    mockServer.use(
      http.get(SEMESTER_COURSES_ENDPOINT, () =>
        HttpResponse.json([
          {
            id: 2,
            code: "CHEM-201",
            name: "Chemistry Foundations",
            description: "Core chemistry principles.",
            credits: 1,
            hoursPerWeek: 5,
            specialization: "Science",
            prerequisite: null,
            courseType: "REGULAR",
            gradeLevelMin: 10,
            gradeLevelMax: 12,
          },
        ]),
      ),
    );

    const courses = await getSemesterCourses();

    expect(courses).toEqual([
      {
        id: 2,
        code: "CHEM-201",
        name: "Chemistry Foundations",
        description: "Core chemistry principles.",
        credits: 1,
        hoursPerWeek: 5,
        specialization: "Science",
        prerequisite: null,
        courseType: "REGULAR",
        gradeLevelMin: 10,
        gradeLevelMax: 12,
        availableForYou: false,
      },
    ]);
  });
});
