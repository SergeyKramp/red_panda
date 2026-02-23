import { CourseHistoryLine } from "features/api";
import styles from "./course-history-table.module.css";

export interface CourseHistoryTableProps {
  courseHistory: CourseHistoryLine[];
}

export function CourseHistoryTable({ courseHistory }: CourseHistoryTableProps) {
  const totalPoints = courseHistory.reduce((sum, line) => {
    const parsedCredits = Number.parseFloat(line.credits);
    return Number.isFinite(parsedCredits) ? sum + parsedCredits : sum;
  }, 0);

  const formattedTotalPoints = totalPoints.toFixed(1);

  return (
    <section aria-label="course history" className={styles.container}>
      <table className={styles.table}>
        <caption className={styles.caption}>Course history</caption>
        <thead>
          <tr>
            <th scope="col">Course</th>
            <th scope="col">Status</th>
            <th scope="col">Credits</th>
          </tr>
        </thead>

        <tbody>
          {courseHistory.length === 0 ? (
            <tr>
              <td className={styles.empty} colSpan={3}>
                No course history available yet.
              </td>
            </tr>
          ) : (
            courseHistory.map((line, index) => (
              <tr key={`${line.courseName}-${line.status}-${index}`}>
                <td>{line.courseName}</td>
                <td>
                  <span
                    className={`${styles.statusBadge} ${
                      line.status.toUpperCase() === "PASSED"
                        ? styles.passed
                        : styles.failed
                    }`}
                  >
                    {line.status}
                  </span>
                </td>
                <td>{line.credits}</td>
              </tr>
            ))
          )}
        </tbody>

        <tfoot>
          <tr>
            <td className={styles.totalLabel} colSpan={2}>
              Total credits
            </td>
            <td className={styles.totalValue}>{formattedTotalPoints}</td>
          </tr>
        </tfoot>
      </table>
    </section>
  );
}
