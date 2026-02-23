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

export type CourseInfo = z.infer<typeof CourseDtoZod>;

export type EnrollmentFailureCode = z.infer<typeof EnrollmentFailureCodeZod>;
export type EnrollmentFailureResponse = z.infer<typeof EnrollmentFailureResponseZod>;
