import {
  API_BASE_URL,
  StudentInfo,
  StudentInfoResponseZod,
} from "features/api";

const STUDENT_INFO_ENDPOINT = `${API_BASE_URL}/api/dashboard/student/info`;

export async function getStudentInfo(): Promise<StudentInfo> {
  const response = await fetch(STUDENT_INFO_ENDPOINT, {
    method: "GET",
    credentials: "include",
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch student info: ${response.status}`);
  }

  return StudentInfoResponseZod.parse(await response.json());
}
