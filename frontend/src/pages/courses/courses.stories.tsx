import type { Meta, StoryObj } from "@storybook/react-webpack5";
import { CourseInfo } from "features/api";
import { expect, userEvent, within } from "storybook/test";
import "../../global.module.css";
import { Courses } from "./courses";

const COURSE_CATALOG: CourseInfo[] = [
  {
    id: 1,
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
  },
  {
    id: 2,
    code: "CHEM-201",
    name: "Chemistry Foundations",
    description: "Core chemistry principles.",
    credits: 1,
    hoursPerWeek: 5,
    specialization: "Science",
    prerequisite: null,
    courseType: "REGULAR",
    gradeLevelMin: 10,
    gradeLevelMax: 12,
  },
  {
    id: 3,
    code: "ALG-102",
    name: "Algebra II",
    description: "Intermediate algebra curriculum.",
    credits: 1,
    hoursPerWeek: 5,
    specialization: "Mathematics",
    prerequisite: "Algebra I",
    courseType: "REGULAR",
    gradeLevelMin: 9,
    gradeLevelMax: 12,
  },
  {
    id: 4,
    code: "LIT-115",
    name: "World Literature",
    description: "Literary works from multiple regions.",
    credits: 1,
    hoursPerWeek: 4,
    specialization: "Humanities",
    prerequisite: null,
    courseType: "REGULAR",
    gradeLevelMin: 9,
    gradeLevelMax: 12,
  },
  {
    id: 5,
    code: "CS-120",
    name: "Computer Science Principles",
    description: "Programming and computational thinking.",
    credits: 1,
    hoursPerWeek: 5,
    specialization: "Technology",
    prerequisite: null,
    courseType: "REGULAR",
    gradeLevelMin: 9,
    gradeLevelMax: 12,
  },
  {
    id: 6,
    code: "ART-130",
    name: "Studio Art",
    description: "Drawing and mixed media studio work.",
    credits: 0.5,
    hoursPerWeek: 3,
    specialization: "Arts",
    prerequisite: null,
    courseType: "ELECTIVE",
    gradeLevelMin: 9,
    gradeLevelMax: 12,
  },
  {
    id: 7,
    code: "HIST-210",
    name: "U.S. History",
    description: "Survey of United States history.",
    credits: 1,
    hoursPerWeek: 4,
    specialization: "Humanities",
    prerequisite: null,
    courseType: "REGULAR",
    gradeLevelMin: 10,
    gradeLevelMax: 12,
  },
  {
    id: 8,
    code: "ENV-205",
    name: "Environmental Systems",
    description: "Study of ecosystems and sustainability.",
    credits: 1,
    hoursPerWeek: 5,
    specialization: "Science",
    prerequisite: "Introduction to Biology",
    courseType: "ELECTIVE",
    gradeLevelMin: 11,
    gradeLevelMax: 12,
  },
];

const SEMESTER_COURSE_CATALOG: CourseInfo[] = [
  COURSE_CATALOG[0],
  COURSE_CATALOG[1],
  COURSE_CATALOG[3],
  COURSE_CATALOG[5],
  COURSE_CATALOG[7],
];

const STUDENT_COURSE_CATALOG: CourseInfo[] = [
  COURSE_CATALOG[0],
  COURSE_CATALOG[2],
  COURSE_CATALOG[3],
  COURSE_CATALOG[5],
  COURSE_CATALOG[6],
];

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

export const Default: Story = {
  args: {
    courses: COURSE_CATALOG,
    semesterCourses: SEMESTER_COURSE_CATALOG,
    studentCourses: STUDENT_COURSE_CATALOG,
  },
};

export const FiltersChangeVisibleCards: Story = {
  args: {
    courses: COURSE_CATALOG,
    semesterCourses: SEMESTER_COURSE_CATALOG,
    studentCourses: STUDENT_COURSE_CATALOG,
  },
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
