import { useQuery } from "@tanstack/react-query";
import { getCourseHistory } from "./course-history";

export const courseHistoryQueryKeys = {
  list: ["course-history", "list"] as const,
};

export interface UseCourseHistoryQueryOptions {
  enabled?: boolean;
}

export function useCourseHistoryQuery({
  enabled = true,
}: UseCourseHistoryQueryOptions = {}) {
  return useQuery({
    queryKey: courseHistoryQueryKeys.list,
    queryFn: getCourseHistory,
    enabled,
  });
}
