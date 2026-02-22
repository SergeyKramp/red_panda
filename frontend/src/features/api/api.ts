/**
 * Shared API utilities and types.
 */
import z from "zod";

export const API_BASE_URL =
  process.env.REACT_BASE_URL ?? "http://localhost:8080";

export const CourseCardInfoZod = z.object({
  code: z.string().min(1),
  name: z.string().min(1),
  credits: z.number().int().nonnegative(),
  specialization: z.string().min(1),
  availableForYou: z.boolean(),
  availableThisSemester: z.boolean(),
});

export const CourseCardInfoListZod = z.array(CourseCardInfoZod);

export type CourseCardInfo = z.infer<typeof CourseCardInfoZod>;
