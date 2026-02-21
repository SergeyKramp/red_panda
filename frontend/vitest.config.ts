import path from "path";
import { defineConfig } from "vitest/config";

export default defineConfig({
  resolve: {
    alias: {
      features: path.resolve(__dirname, "src/features"),
      "test-utils": path.resolve(__dirname, "src/test-utils"),
      pages: path.resolve(__dirname, "src/pages"),
      types: path.resolve(__dirname, "src/types"),
    },
  },
  test: {
    environment: "jsdom",
    setupFiles: "./test/setup-vitest.ts",
    globals: true,
    css: true,
  },
});
