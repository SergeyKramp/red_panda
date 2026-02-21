import "@testing-library/jest-dom/vitest";
import { TextDecoder, TextEncoder } from "util";
import { TransformStream } from "stream/web";
import { afterAll, afterEach, beforeAll } from "vitest";
import { mockServer } from "../src/test-utils/mock-server";

if (!globalThis.TextEncoder) {
  globalThis.TextEncoder = TextEncoder;
}

if (!globalThis.TextDecoder) {
  globalThis.TextDecoder = TextDecoder as typeof globalThis.TextDecoder;
}

if (!globalThis.TransformStream) {
  globalThis.TransformStream = TransformStream as typeof globalThis.TransformStream;
}

beforeAll(() => {
  mockServer.listen({ onUnhandledRequest: "error" });
});

afterEach(() => {
  mockServer.resetHandlers();
});

afterAll(() => {
  mockServer.close();
});
