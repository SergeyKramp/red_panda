import {
  API_BASE_URL,
  EnrollmentFailureCode,
  EnrollmentFailureResponseZod,
} from "features/api";

const ENROLLMENT_ENDPOINT = `${API_BASE_URL}/api/courses/enroll/c`;
const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

export class EnrollmentConflictError extends Error {
  readonly code: EnrollmentFailureCode;
  readonly status: number;

  constructor(code: EnrollmentFailureCode, message: string, status: number) {
    super(message);
    this.name = "EnrollmentConflictError";
    this.code = code;
    this.status = status;
  }
}

export function describeEnrollmentFailure(code: EnrollmentFailureCode): string {
  switch (code) {
    case "INVALID_INPUT":
      return "Enrollment request is invalid. Please refresh and try again.";
    case "GRADE_LEVEL_MISMATCH":
      return "Your grade level is not eligible for this course.";
    case "COURSE_ALREADY_PASSED":
      return "You have already passed this course.";
    case "COURSE_ALREADY_ENROLLED":
      return "You are already enrolled in this course.";
    case "PREREQUISITE_NOT_MET":
      return "You need to complete the prerequisite course first.";
    case "MAX_COURSES_REACHED":
      return "You have already reached the maximum number of active semester courses.";
    case "UNKNOWN":
      return "Enrollment could not be completed due to a course rule conflict.";
    default:
      return "Enrollment could not be completed.";
  }
}

function getCookieValue(cookieName: string): string | null {
  const targetPrefix = `${cookieName}=`;
  const cookies = document.cookie.split(";").map((cookiePart) => cookiePart.trim());
  const matchedCookie = cookies.find((cookiePart) => cookiePart.startsWith(targetPrefix));

  if (!matchedCookie) {
    return null;
  }

  return decodeURIComponent(matchedCookie.slice(targetPrefix.length));
}

export async function enrollInCourse(courseId: number): Promise<void> {
  const csrfToken = getCookieValue(CSRF_COOKIE_NAME);
  const response = await fetch(`${ENROLLMENT_ENDPOINT}/${courseId}`, {
    method: "POST",
    credentials: "include",
    headers: csrfToken ? { [CSRF_HEADER_NAME]: csrfToken } : undefined,
  });

  if (response.ok) {
    return;
  }

  if (response.status === 409) {
    const responseBody = await response.json().catch(() => null);
    const parsedResponse = EnrollmentFailureResponseZod.safeParse(responseBody);
    const enrollmentCode = parsedResponse.success
      ? parsedResponse.data.messageCode
      : "UNKNOWN";
    const fallbackMessage = describeEnrollmentFailure(enrollmentCode);

    throw new EnrollmentConflictError(enrollmentCode, fallbackMessage, 409);
  }

  throw new Error(`Failed to enroll in course: ${response.status}`);
}
