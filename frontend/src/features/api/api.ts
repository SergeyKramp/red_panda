/**
 * Shared API utilities and types.
 */
import z from "zod";

export const API_BASE_URL =
  process.env.REACT_BASE_URL ?? "http://localhost:8080";

// Matches backend CourseDTO returned by /api/courses/.
export const CourseDtoZod = z.object({
  id: z.number().int(),
  code: z.string().min(1),
  name: z.string().min(1),
  description: z.string(),
  credits: z.number().nonnegative(),
  hoursPerWeek: z.number().int().nonnegative(),
  specialization: z.string().min(1),
  prerequisite: z.string().nullable(),
  courseType: z.string().min(1),
  gradeLevelMin: z.number().int(),
  gradeLevelMax: z.number().int(),
});

export const CourseDtoListZod = z.array(CourseDtoZod);

export const EnrollmentFailureCodeZod = z.enum([
  "INVALID_INPUT",
  "GRADE_LEVEL_MISMATCH",
  "COURSE_ALREADY_PASSED",
  "COURSE_ALREADY_ENROLLED",
  "PREREQUISITE_NOT_MET",
  "MAX_COURSES_REACHED",
  "UNKNOWN",
]);

export const EnrollmentFailureResponseZod = z.object({
  messageCode: EnrollmentFailureCodeZod,
  courseId: z.number().int(),
});

// Matches backend CourseHistoryDTO returned inside /api/dashboard/student/course-history.
export const CourseHistoryLineDtoZod = z.object({
  courseName: z.string().min(1),
  credits: z.string().min(1),
  status: z.string().min(1),
});

export const CourseHistoryResponseZod = z.object({
  courseHistory: z.array(CourseHistoryLineDtoZod),
});

// Matches backend EnrolledCourseDTO returned inside /api/dashboard/student/enrolled-courses.
export const EnrolledCourseLineDtoZod = z.object({
  courseName: z.string().min(1),
  credits: z.string().min(1),
});

export const EnrolledCoursesResponseZod = z.object({
  enrolledCourses: z.array(EnrolledCourseLineDtoZod),
});

export type CourseInfo = z.infer<typeof CourseDtoZod>;
export type CourseHistoryLine = z.infer<typeof CourseHistoryLineDtoZod>;
export type CourseHistoryResponse = z.infer<typeof CourseHistoryResponseZod>;
export type EnrolledCourseLine = z.infer<typeof EnrolledCourseLineDtoZod>;
export type EnrolledCoursesResponse = z.infer<typeof EnrolledCoursesResponseZod>;

export type EnrollmentFailureCode = z.infer<typeof EnrollmentFailureCodeZod>;
export type EnrollmentFailureResponse = z.infer<typeof EnrollmentFailureResponseZod>;
