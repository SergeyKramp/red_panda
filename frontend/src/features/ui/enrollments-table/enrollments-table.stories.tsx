import type { Meta, StoryObj } from "@storybook/react-webpack5";
import type { EnrolledCourseLine } from "features/api";
import "../../../global.module.css";
import { EnrollmentsTable } from "./enrollments-table";

const DEFAULT_ENROLLED_COURSES: EnrolledCourseLine[] = [
  {
    courseName: "English Composition",
    credits: "3.0",
  },
  {
    courseName: "Biology I",
    credits: "2.0",
  },
  {
    courseName: "World History",
    credits: "2.0",
  },
];

const meta: Meta<typeof EnrollmentsTable> = {
  title: "Features/UI/EnrollmentsTable",
  component: EnrollmentsTable,
  decorators: [
    (Story) => (
      <div style={{ maxWidth: "44rem", padding: "1rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    enrolledCourses: DEFAULT_ENROLLED_COURSES,
  },
};

export const Empty: Story = {
  args: {
    enrolledCourses: [],
  },
};
