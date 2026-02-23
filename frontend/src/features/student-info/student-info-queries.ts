import { useQuery } from "@tanstack/react-query";
import { getStudentInfo } from "./student-info";

export const studentInfoQueryKeys = {
  details: ["student-info", "details"] as const,
};

export interface UseStudentInfoQueryOptions {
  enabled?: boolean;
}

export function useStudentInfoQuery({
  enabled = true,
}: UseStudentInfoQueryOptions = {}) {
  return useQuery({
    queryKey: studentInfoQueryKeys.details,
    queryFn: getStudentInfo,
    enabled,
  });
}
