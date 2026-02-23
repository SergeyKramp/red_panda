import type { Meta, StoryObj } from "@storybook/react-webpack5";
import type { CourseHistoryLine } from "features/api";
import "../../../global.module.css";
import { CourseHistoryTable } from "./course-history-table";

const DEFAULT_HISTORY: CourseHistoryLine[] = [
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
];

const meta: Meta<typeof CourseHistoryTable> = {
  title: "Features/UI/CourseHistoryTable",
  component: CourseHistoryTable,
  decorators: [
    (Story) => (
      <div style={{ maxWidth: "52rem", padding: "1rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    courseHistory: DEFAULT_HISTORY,
  },
};

export const Empty: Story = {
  args: {
    courseHistory: [],
  },
};
