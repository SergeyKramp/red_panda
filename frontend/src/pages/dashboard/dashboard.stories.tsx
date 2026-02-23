import type { Meta, StoryObj } from "@storybook/react-webpack5";
import { API_BASE_URL } from "features/api";
import { http, HttpResponse } from "msw";
import "../../global.module.css";
import { Dashboard } from "./dashboard";

const COURSE_HISTORY_ENDPOINT =
  `${API_BASE_URL}/api/dashboard/student/course-history`;

const defaultCourseHistory = {
  courseHistory: [
    {
      courseName: "English Composition",
      credits: "3.0",
      status: "PASSED",
    },
    {
      courseName: "World History",
      credits: "2.0",
      status: "FAILED",
    },
    {
      courseName: "Biology I",
      credits: "3.0",
      status: "PASSED",
    },
  ],
};

const meta: Meta<typeof Dashboard> = {
  title: "Pages/Dashboard",
  component: Dashboard,
  decorators: [
    (Story) => (
      <div style={{ minHeight: "44rem", padding: "1rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    msw: {
      handlers: [
        http.get(COURSE_HISTORY_ENDPOINT, () =>
          HttpResponse.json(defaultCourseHistory),
        ),
      ],
    },
  },
};

export const Empty: Story = {
  parameters: {
    msw: {
      handlers: [
        http.get(COURSE_HISTORY_ENDPOINT, () =>
          HttpResponse.json({ courseHistory: [] }),
        ),
      ],
    },
  },
};

export const ErrorState: Story = {
  parameters: {
    msw: {
      handlers: [
        http.get(COURSE_HISTORY_ENDPOINT, () => new HttpResponse(null, { status: 500 })),
      ],
    },
  },
};

export const LoadingState: Story = {
  parameters: {
    msw: {
      handlers: [
        http.get(COURSE_HISTORY_ENDPOINT, async () => {
          await new Promise(() => undefined);
          return HttpResponse.json(defaultCourseHistory);
        }),
      ],
    },
  },
};
