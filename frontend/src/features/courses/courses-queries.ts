import { useQuery } from "@tanstack/react-query";
import { getCourses, getSemesterCourses, getStudentCourses } from "./courses";

export const coursesQueryKeys = {
  list: ["courses", "list"] as const,
  semesterList: ["courses", "semester-list"] as const,
  studentList: ["courses", "student-list"] as const,
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

export function useSemesterCoursesQuery({
  enabled = true,
}: UseCoursesQueryOptions = {}) {
  return useQuery({
    queryKey: coursesQueryKeys.semesterList,
    queryFn: getSemesterCourses,
    enabled,
  });
}

export function useStudentCoursesQuery({
  enabled = true,
}: UseCoursesQueryOptions = {}) {
  return useQuery({
    queryKey: coursesQueryKeys.studentList,
    queryFn: getStudentCourses,
    enabled,
  });
}
