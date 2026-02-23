import {
  API_BASE_URL,
  CourseHistoryLine,
  CourseHistoryResponseZod,
} from "features/api";

const COURSE_HISTORY_ENDPOINT = `${API_BASE_URL}/api/dashboard/student/course-history`;

export async function getCourseHistory(): Promise<CourseHistoryLine[]> {
  const response = await fetch(COURSE_HISTORY_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch course history: ${response.status}`);
  }

  const parsedResponse = CourseHistoryResponseZod.parse(await response.json());
  return parsedResponse.courseHistory;
}
