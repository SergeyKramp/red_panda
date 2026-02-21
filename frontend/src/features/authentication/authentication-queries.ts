import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getAuthenticationStatus, login, type LoginRequest } from "./authentication";

export const authenticationQueryKeys = {
  me: ["authentication", "me"] as const,
};

export function useAuthenticationStatusQuery() {
  return useQuery({
    queryKey: authenticationQueryKeys.me,
    queryFn: getAuthenticationStatus,
  });
}

export function useLoginMutation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: LoginRequest) => login(payload),
    onSuccess: async (response) => {
      if (!response.ok) {
        return;
      }

      await queryClient.invalidateQueries({
        queryKey: authenticationQueryKeys.me,
      });
    },
  });
}
