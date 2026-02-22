import type { Meta, StoryObj } from "@storybook/react-webpack5";
import type { CourseInfo } from "features/api";
import "../../../global.module.css";
import { CourseGrid } from "./course-grid";

function createCourses(count: number): CourseInfo[] {
  return Array.from({ length: count }, (_, index) => ({
    id: index + 1,
    code: `CRS-${index + 101}`,
    name: `Course ${index + 1}`,
    description: `Description for course ${index + 1}`,
    credits: (index % 2) + 1,
    hoursPerWeek: 4,
    specialization:
      index % 3 === 0 ? "Science" : index % 3 === 1 ? "Math" : "Humanities",
    prerequisite: null,
    courseType: index % 2 === 0 ? "REGULAR" : "ELECTIVE",
    gradeLevelMin: 9,
    gradeLevelMax: 12,
    availableForYou: index % 2 === 0,
  }));
}

const meta: Meta<typeof CourseGrid> = {
  title: "Features/UI/CourseGrid",
  component: CourseGrid,
  decorators: [
    (Story) => (
      <div style={{ maxWidth: "64rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const TwoCards: Story = {
  args: {
    courses: createCourses(2),
  },
};

export const EightCards: Story = {
  args: {
    courses: createCourses(8),
  },
};

export const TwentyCards: Story = {
  args: {
    courses: createCourses(20),
  },
};
