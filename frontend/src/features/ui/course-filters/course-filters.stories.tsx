import type { Meta, StoryObj } from "@storybook/react-webpack5";
import { useState } from "react";
import "../../../global.module.css";
import { CourseFilter, CourseFilters } from "./course-filters";

const meta: Meta<typeof CourseFilters> = {
  title: "Features/UI/CourseFilters",
  component: CourseFilters,
  args: {
    activeFilter: "all",
    onFilterChange: () => {},
  },
  decorators: [
    (Story) => (
      <div style={{ padding: "0.5rem" }}>
        <Story />
      </div>
    ),
  ],
};

export default meta;

type Story = StoryObj<typeof meta>;

function InteractiveFilters() {
  const [activeFilter, setActiveFilter] = useState<CourseFilter>("all");

  return (
    <CourseFilters activeFilter={activeFilter} onFilterChange={setActiveFilter} />
  );
}

export const Default: Story = {};

export const ThisSemesterSelected: Story = {
  args: {
    activeFilter: "this-semester",
  },
};

export const AvailableForYouSelected: Story = {
  args: {
    activeFilter: "available-for-you",
  },
};

export const Interactive: Story = {
  render: () => <InteractiveFilters />,
};
