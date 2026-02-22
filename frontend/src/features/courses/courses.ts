import {
  API_BASE_URL,
  CourseInfo,
  CourseDto,
  CourseDtoListZod,
} from "features/api";

const COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/`;
const SEMESTER_COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/semester`;

function mapCourseDtoToCourseCardInfo(course: CourseDto): CourseInfo {
  return {
    ...course,
    availableForYou: false,
  };
}

export async function getCourses(): Promise<CourseInfo[]> {
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

export async function getSemesterCourses(): Promise<CourseInfo[]> {
  const response = await fetch(SEMESTER_COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch semester courses: ${response.status}`);
  }

  const data = CourseDtoListZod.parse(await response.json());
  return data.map(mapCourseDtoToCourseCardInfo);
}
