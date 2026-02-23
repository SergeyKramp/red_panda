import { useMutation, useQueryClient } from "@tanstack/react-query";
import { coursesQueryKeys } from "features/courses";
import { enrollInCourse } from "./enrollment";

export function useEnrollInCourseMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (courseId: number) => enrollInCourse(courseId),
    onSuccess: async (result) => {
      if (!result.ok) {
        return;
      }

      await Promise.all([
        queryClient.invalidateQueries({ queryKey: coursesQueryKeys.list }),
        queryClient.invalidateQueries({ queryKey: coursesQueryKeys.semesterList }),
        queryClient.invalidateQueries({ queryKey: coursesQueryKeys.studentList }),
      ]);
    },
  });
}
