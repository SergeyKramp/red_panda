import {
  API_BASE_URL,
  CourseInfo,
  CourseDtoListZod,
} from "features/api";

const COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/`;
const SEMESTER_COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/semester`;
const STUDENT_COURSES_ENDPOINT = `${API_BASE_URL}/api/courses/student`;

export async function getCourses(): Promise<CourseInfo[]> {
  const response = await fetch(COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch courses: ${response.status}`);
  }

  return CourseDtoListZod.parse(await response.json());
}

export async function getSemesterCourses(): Promise<CourseInfo[]> {
  const response = await fetch(SEMESTER_COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch semester courses: ${response.status}`);
  }

  return CourseDtoListZod.parse(await response.json());
}

export async function getStudentCourses(): Promise<CourseInfo[]> {
  const response = await fetch(STUDENT_COURSES_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch student courses: ${response.status}`);
  }

  return CourseDtoListZod.parse(await response.json());
}
