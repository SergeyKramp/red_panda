import {
  API_BASE_URL,
  EnrollmentFailureCode,
  EnrollmentFailureResponseZod,
} from "features/api";

const ENROLLMENT_ENDPOINT = `${API_BASE_URL}/api/courses/enroll/c`;
const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

const enrollmentFailureMessageByCode: Record<EnrollmentFailureCode, string> = {
  INVALID_INPUT: "Enrollment request is invalid. Please refresh and try again.",
  GRADE_LEVEL_MISMATCH: "Your grade level is not eligible for this course.",
  COURSE_ALREADY_PASSED: "You have already passed this course.",
  COURSE_ALREADY_ENROLLED: "You are already enrolled in this course.",
  PREREQUISITE_NOT_MET: "You need to complete the prerequisite course first.",
  MAX_COURSES_REACHED: "You have already reached the maximum number of active semester courses.",
  UNKNOWN: "Enrollment could not be completed due to a course rule conflict.",
};

export function getEnrollmentFailureMessage(code: EnrollmentFailureCode): string {
  return enrollmentFailureMessageByCode[code] ?? enrollmentFailureMessageByCode.UNKNOWN;
}

export type EnrollInCourseResult =
  | { ok: true }
  | { ok: false; code: EnrollmentFailureCode };

function getCookieValue(cookieName: string): string | null {
  const targetPrefix = `${cookieName}=`;
  const cookies = document.cookie.split(";").map((cookiePart) => cookiePart.trim());
  const matchedCookie = cookies.find((cookiePart) => cookiePart.startsWith(targetPrefix));

  if (!matchedCookie) {
    return null;
  }

  return decodeURIComponent(matchedCookie.slice(targetPrefix.length));
}

export async function enrollInCourse(courseId: number): Promise<EnrollInCourseResult> {
  const csrfToken = getCookieValue(CSRF_COOKIE_NAME);
  const response = await fetch(`${ENROLLMENT_ENDPOINT}/${courseId}`, {
    method: "POST",
    credentials: "include",
    headers: csrfToken ? { [CSRF_HEADER_NAME]: csrfToken } : undefined,
  });

  if (response.ok) {
    return { ok: true };
  }

  if (response.status === 409) {
    const responseBody = await response.json().catch(() => null);
    const parsedResponse = EnrollmentFailureResponseZod.safeParse(responseBody);
    const code = parsedResponse.success
      ? parsedResponse.data.messageCode
      : "UNKNOWN";
    return { ok: false, code };
  }

  throw new Error(`Failed to enroll in course: ${response.status}`);
}
