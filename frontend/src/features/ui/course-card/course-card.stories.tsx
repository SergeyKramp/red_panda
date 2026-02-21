import type { Meta, StoryObj } from "@storybook/react-webpack5";
import "../../../global.module.css";
import { CourseCard } from "./course-card";

const meta: Meta<typeof CourseCard> = {
  title: "Features/UI/CourseCard",
  component: CourseCard,
  args: {
    name: "Introduction to Biology",
    code: "BIO-101",
    credits: 1,
    specialization: "Science",
  },
  decorators: [
    (Story) => (
      <div style={{ width: "300px" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
