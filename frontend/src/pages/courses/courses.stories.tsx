import type { Meta, StoryObj } from "@storybook/react-webpack5";
import { expect, userEvent, within } from "storybook/test";
import "../../global.module.css";
import { Courses } from "./courses";

const meta: Meta<typeof Courses> = {
  title: "Pages/Courses",
  component: Courses,
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

export const Default: Story = {};

export const FiltersChangeVisibleCards: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);

    await expect(canvas.getByText("Chemistry Foundations")).toBeInTheDocument();
    await expect(canvas.getByText("Algebra II")).toBeInTheDocument();

    await userEvent.click(canvas.getByRole("button", { name: "This Semester" }));
    await expect(canvas.getByText("Chemistry Foundations")).toBeInTheDocument();
    await expect(canvas.queryByText("Algebra II")).not.toBeInTheDocument();

    await userEvent.click(
      canvas.getByRole("button", { name: "Available for you" }),
    );
    await expect(canvas.getByText("Algebra II")).toBeInTheDocument();
    await expect(canvas.queryByText("Chemistry Foundations")).not.toBeInTheDocument();
  },
};
