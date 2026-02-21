import { create } from "zustand";

type AuthenticationState = {
  authenticated: boolean | null;
  setAuthenticated: (authenticated: boolean | null) => void;
};

export const useAuthenticationStore = create<AuthenticationState>((set) => ({
  authenticated: null,
  setAuthenticated: (authenticated) => set({ authenticated }),
}));
