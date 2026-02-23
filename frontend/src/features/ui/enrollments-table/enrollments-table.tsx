import { EnrolledCourseLine } from "features/api";
import styles from "./enrollments-table.module.css";

export interface EnrollmentsTableProps {
  enrolledCourses: EnrolledCourseLine[];
}

export function EnrollmentsTable({ enrolledCourses }: EnrollmentsTableProps) {
  const totalCredits = enrolledCourses.reduce((sum, line) => {
    const parsedCredits = Number.parseFloat(line.credits);
    return Number.isFinite(parsedCredits) ? sum + parsedCredits : sum;
  }, 0);

  const formattedTotalCredits = totalCredits.toFixed(1);

  return (
    <section aria-label="enrolled courses" className={styles.container}>
      <table className={styles.table}>
        <caption className={styles.caption}>Enrolled courses</caption>
        <thead>
          <tr>
            <th scope="col">Course</th>
            <th scope="col">Credits</th>
          </tr>
        </thead>

        <tbody>
          {enrolledCourses.length === 0 ? (
            <tr>
              <td className={styles.empty} colSpan={2}>
                No active enrollments yet.
              </td>
            </tr>
          ) : (
            enrolledCourses.map((line, index) => (
              <tr key={`${line.courseName}-${index}`}>
                <td>{line.courseName}</td>
                <td>{line.credits}</td>
              </tr>
            ))
          )}
        </tbody>

        <tfoot>
          <tr>
            <td className={styles.totalLabel}>Total credits</td>
            <td className={styles.totalValue}>{formattedTotalCredits}</td>
          </tr>
        </tfoot>
      </table>
    </section>
  );
}
