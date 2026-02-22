import {
  API_BASE_URL,
  CourseCardInfo,
  CourseDto,
  CourseDtoListZod,
} from "features/api";

const COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/`;

function mapCourseDtoToCourseCardInfo(course: CourseDto): CourseCardInfo {
  return {
    ...course,
    availableForYou: false,
    availableThisSemester: false,
  };
}

export async function getCourses(): Promise<CourseCardInfo[]> {
  const response = await fetch(COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch courses: ${response.status}`);
  }

  const data = CourseDtoListZod.parse(await response.json());
  return data.map(mapCourseDtoToCourseCardInfo);
}
