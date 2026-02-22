# AGENTS Guide

## Project Context

This repository is an interview assignment for a full-stack course planning system.

- Backend: Spring Boot (Java 17, Maven, SQLite)
- Frontend: React written in TypeScript

Prioritize correctness, clarity, and practical delivery over over-engineering.

## Core Expectations

- Implement working features end-to-end.
- Enforce business rules with clear validation and error handling.
- Keep code readable, testable, and easy to review.
- Prefer small, focused changes over broad refactors unless necessary.

## Backend Conventions (Spring Boot / Java)

- Use Spring Boot patterns already present in this repo.
- Prefer `var` for local variable declarations when the type is obvious from the right-hand side.
- Keep DTOs, domain models, repositories, services, and controllers separated by responsibility.
- Return clear API errors for validation failures and rule violations.
- Preserve database integrity and avoid hidden side effects.
- When writing tests, include a gerkin-style comment describing the scenario being tested, the expected outcome, and any important edge cases.
  For example:
  ```java
  /**
   * Given: a student with a completed prerequisite course
    * When: the student attempts to enroll in a course that requires that prerequisite
    * Then: the enrollment should succeed
  */
  @Test
  public void testEnrollmentWithPrerequisite() {
      // test implementation
  }
  ```



## Frontend Conventions (React / TypeScript)

- The React app is TypeScript-first: use `.ts`/`.tsx` and explicit types for public interfaces.
- Keep state predictable and centralized through the chosen state management approach.
- Handle loading, success, and error states explicitly in the UI.
- Keep components focused; move API and state logic out of view components when practical.
- Use kebab-case for file names for all file names, not PascalCase or camelCase. If you see a file that looks like that
  , please rename it to kebab-case.
- We use CSS modules for styling. Keep styles scoped to components and avoid global CSS when possible.
- When writing CSS, use `rem` instead of `px` units for font sizes and spacing to ensure accessibility and responsiveness.
- The name of the school we are making this for is called maplewood. So when creating colors use colors that are reminiscent of maplewood, such as warm browns, soft oranges, and muted greens. For example:
  - Primary color: #A0522D (Sienna)
  - Secondary color: #FF8C00 (Dark Orange)
  - Accent color: #556B2F (Dark Olive Green)
  - Background color: #F5F5DC (Beige)
  - Text color: #333333 (Dark Gray)
- Use the `global.module.css` CSS module before writing any new CSS. If you need to add a new color, add it to the `:root` selector in `global.module.css` and use the variable in your component's CSS module, but first check if the color you want to use is already defined in `global.module.css` before adding a new one.
- Use semantic HTML elements and ARIA attributes to ensure accessibility.

## Quality Bar

- Validate prerequisites, schedule conflicts, course limits, and graduation-related logic.
- Add or update tests when behavior changes.
- Avoid dead code, vague naming, and silent failures.
- Document notable tradeoffs briefly in PRs or commit messages.
