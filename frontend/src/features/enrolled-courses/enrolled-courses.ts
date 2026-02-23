import {
  API_BASE_URL,
  EnrolledCourseLine,
  EnrolledCoursesResponseZod,
} from "features/api";

const ENROLLED_COURSES_ENDPOINT = `${API_BASE_URL}/api/dashboard/student/enrolled-courses`;

export async function getEnrolledCourses(): Promise<EnrolledCourseLine[]> {
  const response = await fetch(ENROLLED_COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch enrolled courses: ${response.status}`);
  }

  const parsedResponse = EnrolledCoursesResponseZod.parse(await response.json());
  return parsedResponse.enrolledCourses;
}
