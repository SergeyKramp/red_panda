import { useQuery } from "@tanstack/react-query";
import { getEnrolledCourses } from "./enrolled-courses";

export const enrolledCoursesQueryKeys = {
  list: ["enrolled-courses", "list"] as const,
};

export interface UseEnrolledCoursesQueryOptions {
  enabled?: boolean;
}

export function useEnrolledCoursesQuery({
  enabled = true,
}: UseEnrolledCoursesQueryOptions = {}) {
  return useQuery({
    queryKey: enrolledCoursesQueryKeys.list,
    queryFn: getEnrolledCourses,
    enabled,
  });
}
