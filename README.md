# Maplewood Course Planning System


## TL;DR

### What I completed

- There is a working course catalog where students can browse courses and see details. There are filters for getting courses by semester and eligible courses for the logged-in student.
- There is an enrollment workflow with validation for grade level, prerequisites, already-passed prevention, already-enrolled prevention, and a max of 5 courses per active semester.
- There is a student dashboard where students can see their info, course history, currently enrolled courses, and credit progress.

### What is still left

- The calendar page is a placeholder and the scheduling workflow is not implemented yet.
- There are some GitHub issues that list improvements I did not have time to implement.

## How to run the project

Open this repository in the dev container or install the prerequisites locally.

### 1. Run backend

```bash
cd backend
mvn spring-boot:run
```

Backend URL: `http://localhost:8080`

### 2. Run frontend

```bash
cd frontend
npm install
npm start
```
Frontend URL: `http://localhost:3000`

### 3. Login

- Username: `root`
- Password: `rootPassword`

## Architecture Choices

### Backend architecture

- Typical Spring Boot layered structure: `controllers -> services -> repositories -> domain`.
- Spring Data JPA repositories handle data access and custom queries.
- Business rules are centralized in `StudentService.canTakeCourse(...)` and reused by `CourseService`.
- Session-based auth with Spring Security + CSRF protection for SPA requests.
- SQLite is used for persisted local data; H2 is used in tests.

### Frontend architecture

- Feature-first organization under `src/features`.
- Main application pages are under `src/pages`.
- Server state is centralized using `@tanstack/react-query` (queries + mutations + cache invalidation).
- Zod schemas are used to validate backend payloads at runtime. See `src/features/api/api.ts` for shared DTO schemas/types.
- UI components are split into reusable building blocks under `src/features/ui`.
- Styling is done with CSS Modules to keep styles scoped per component.
- Storybook is used to build and review UI components in isolation.
- MSW is used in frontend unit tests and Storybook stories to mock API responses.
- Routing is page-based (`dashboard`, `courses`, `calendar`) with a shared layout/navigation shell.

## Important Files

### Backend

- `backend/src/main/java/com/maplewood/config/SecurityConfig.java` (auth, sessions, CSRF)
- `backend/src/main/java/com/maplewood/controllers/CourseController.java` (courses + enrollment API)
- `backend/src/main/java/com/maplewood/controllers/StudentDashboardController.java` (dashboard APIs)
- `backend/src/main/java/com/maplewood/services/StudentService.java` (core enrollment rule checks)

### Frontend

- `frontend/src/App.tsx` (auth gate + routes)
- `frontend/src/features/query-client/query-client.ts` (React Query setup)
- `frontend/src/features/api/api.ts` (shared API DTO schemas/types)
- `frontend/src/pages/dashboard/dashboard.tsx` (dashboard UI/state)
- `frontend/src/pages/courses/courses.tsx` (course browse/filter experience)
- `frontend/src/features/enrollment/enrollment.ts` (enroll API + error mapping)

## Running tests

### Backend tests

```bash
cd backend
mvn test
```

### Frontend tests

```bash
cd frontend
npm test
```

### Frontend typecheck

```bash
cd frontend
npm run typecheck
```

### Storybook

```bash
cd frontend
npm run storybook
```
