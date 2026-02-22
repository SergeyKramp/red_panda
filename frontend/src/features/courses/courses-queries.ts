import { useQuery } from "@tanstack/react-query";
import { getCourses } from "./courses";

export const coursesQueryKeys = {
  list: ["courses", "list"] as const,
};

export interface UseCoursesQueryOptions {
  enabled?: boolean;
}

export function useCoursesQuery({ enabled = true }: UseCoursesQueryOptions = {}) {
  return useQuery({
    queryKey: coursesQueryKeys.list,
    queryFn: getCourses,
    enabled,
  });
}
