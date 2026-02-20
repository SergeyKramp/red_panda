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

## Frontend Conventions (React / TypeScript)

- The React app is TypeScript-first: use `.ts`/`.tsx` and explicit types for public interfaces.
- Keep state predictable and centralized through the chosen state management approach.
- Handle loading, success, and error states explicitly in the UI.
- Keep components focused; move API and state logic out of view components when practical.

## Quality Bar

- Validate prerequisites, schedule conflicts, course limits, and graduation-related logic.
- Add or update tests when behavior changes.
- Avoid dead code, vague naming, and silent failures.
- Document notable tradeoffs briefly in PRs or commit messages.
