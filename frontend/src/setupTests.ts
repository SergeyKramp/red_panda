import { mockServer } from "test-utils/mock-server";
import { beforeAll, afterEach, afterAll } from "vitest";

beforeAll(() => mockServer.listen());
afterEach(() => mockServer.resetHandlers());
afterAll(() => mockServer.close());
