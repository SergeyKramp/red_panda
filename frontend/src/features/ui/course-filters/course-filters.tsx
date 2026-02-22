import styles from "./course-filters.module.css";

export type CourseFilter = "all" | "this-semester" | "available-for-you";

interface FilterOption {
  label: string;
  value: CourseFilter;
}

const FILTER_OPTIONS: FilterOption[] = [
  { label: "All", value: "all" },
  { label: "This Semester", value: "this-semester" },
  { label: "Available for you", value: "available-for-you" },
];

export interface CourseFiltersProps {
  activeFilter: CourseFilter;
  onFilterChange: (filter: CourseFilter) => void;
}

export function CourseFilters({
  activeFilter,
  onFilterChange,
}: CourseFiltersProps) {
  return (
    <ul className={styles.filterPills} aria-label="Course filters">
      {FILTER_OPTIONS.map((option) => {
        const isActive = activeFilter === option.value;

        return (
          <li key={option.value}>
            <button
              aria-pressed={isActive}
              className={`${styles.pill} ${isActive ? styles.active : ""}`}
              onClick={() => onFilterChange(option.value)}
              type="button"
            >
              {option.label}
            </button>
          </li>
        );
      })}
    </ul>
  );
}
