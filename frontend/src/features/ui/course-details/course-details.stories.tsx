import type { Meta, StoryObj } from "@storybook/react-webpack5";
import type { CourseInfo } from "features/api";
import { API_BASE_URL } from "features/api";
import { http, HttpResponse } from "msw";
import { expect, userEvent, within } from "storybook/test";
import "../../../global.module.css";
import { CourseDetails } from "./course-details";

const ENROLLMENT_ENDPOINT = `${API_BASE_URL}/api/courses/enroll/c`;

const SAMPLE_COURSE: CourseInfo = {
  id: 101,
  code: "BIO-101",
  name: "Introduction to Biology",
  description: "Foundational biology topics.",
  credits: 1,
  hoursPerWeek: 5,
  specialization: "Science",
  prerequisite: null,
  courseType: "REGULAR",
  gradeLevelMin: 9,
  gradeLevelMax: 12,
};

const meta: Meta<typeof CourseDetails> = {
  title: "Features/UI/CourseDetails",
  component: CourseDetails,
  decorators: [
    (Story) => (
      <div style={{ maxWidth: "32rem", minHeight: "36rem", padding: "1rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    course: SAMPLE_COURSE,
  },
};

export const PendingAfterClick: Story = {
  args: {
    course: SAMPLE_COURSE,
  },
  parameters: {
    msw: {
      handlers: [
        http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, async () => {
          await new Promise((resolve) => setTimeout(resolve, 1200));
          return new HttpResponse(null, { status: 200 });
        }),
      ],
    },
  },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    await userEvent.click(canvas.getByRole("button", { name: "Sign Up for Course" }));
    await expect(
      canvas.getByRole("button", { name: "Signing up..." }),
    ).toBeInTheDocument();
  },
};

export const SuccessAfterClick: Story = {
  args: {
    course: SAMPLE_COURSE,
  },
  parameters: {
    msw: {
      handlers: [
        http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, async () => {
          return new HttpResponse(null, { status: 200 });
        }),
      ],
    },
  },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    await userEvent.click(canvas.getByRole("button", { name: "Sign Up for Course" }));
    await expect(
      await canvas.findByText("Enrollment successful. Your available courses are being refreshed."),
    ).toBeInTheDocument();
  },
};

export const ConflictAfterClick: Story = {
  args: {
    course: SAMPLE_COURSE,
  },
  parameters: {
    msw: {
      handlers: [
        http.post(`${ENROLLMENT_ENDPOINT}/:courseId`, async () =>
          HttpResponse.json(
            {
              messageCode: "PREREQUISITE_NOT_MET",
              courseId: 101,
            },
            { status: 409 },
          ),
        ),
      ],
    },
  },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    await userEvent.click(canvas.getByRole("button", { name: "Sign Up for Course" }));
    await expect(
      await canvas.findByText("You need to complete the prerequisite course first."),
    ).toBeInTheDocument();
  },
};
