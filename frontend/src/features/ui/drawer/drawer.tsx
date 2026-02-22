import { ReactNode, useEffect, useId } from "react";
import styles from "./drawer.module.css";

export interface DrawerProps {
  isOpen: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
  subtitle?: string;
  closeLabel?: string;
}

export function Drawer({
  isOpen,
  title,
  onClose,
  children,
  subtitle,
  closeLabel = "Close drawer",
}: DrawerProps) {
  const titleId = useId();

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        onClose();
      }
    };

    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    window.addEventListener("keydown", onKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      window.removeEventListener("keydown", onKeyDown);
    };
  }, [isOpen, onClose]);

  return (
    <div
      aria-hidden={!isOpen}
      className={`${styles.root} ${isOpen ? styles.open : styles.closed}`}
    >
      <div
        aria-hidden="true"
        className={styles.backdrop}
        onClick={onClose}
      />

      <aside
        aria-labelledby={titleId}
        aria-modal={isOpen}
        className={styles.panel}
        role="dialog"
      >
        <header className={styles.header}>
          <div className={styles.heading}>
            <h2 className={styles.title} id={titleId}>
              {title}
            </h2>
            {subtitle ? <p className={styles.subtitle}>{subtitle}</p> : null}
          </div>
          <button
            aria-label={closeLabel}
            className={styles.closeButton}
            onClick={onClose}
            type="button"
          >
            Close
          </button>
        </header>

        <div className={styles.content}>{children}</div>
      </aside>
    </div>
  );
}
