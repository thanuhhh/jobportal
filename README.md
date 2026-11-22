# JobPortal

A full stack job portal where job seekers search and apply for jobs, recruiters post jobs and manage applicants, and admins moderate the platform.

**Live demo:** _add your Vercel link_
**API docs (Swagger):** _add your backend link_/swagger-ui.html

## Features

**Job seeker**
- Register and log in (JWT)
- Search jobs by keyword, location, category, type and salary, with pagination
- Apply (duplicate applications blocked), track status, save jobs
- Profile with skills and education, PDF resume upload (2 MB limit, content checked)

**Recruiter**
- Company profile, post / edit / close / delete jobs
- View applicants, change status (Applied, Shortlisted, Rejected, Hired), open resumes

**Admin**
- Approve or delete jobs, block or unblock users, manage categories, platform stats
- Admin account is seeded, not self-registered

**General**
- Role-based access control, blocked users are cut off immediately
- Email notifications on new applications and status changes
- Global exception handling and input validation
- JUnit and Mockito unit tests, Swagger / OpenAPI docs

## Tech stack

- Backend: Java 17, Spring Boot, Spring Security, JWT, Spring Data JPA / Hibernate, MySQL, Maven
- Frontend: React (Vite), React Router, Axios
- Testing and docs: JUnit 5, Mockito, springdoc OpenAPI
- Deployment: Docker, Render, Vercel

## Run locally

Requirements: JDK 17, Node.js, MySQL.

1. Create the database:

        CREATE DATABASE jobportal_db;

2. Create `backend/src/main/resources/application.properties` with your own values:

        spring.datasource.url=jdbc:mysql://localhost:3306/jobportal_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
        spring.datasource.username=root
        spring.datasource.password=YOUR_PASSWORD
        spring.jpa.hibernate.ddl-auto=update
        app.jwt.secret=BASE64_SECRET_FROM_openssl_rand_-base64_32
        app.jwt.expiration-ms=86400000
        app.upload-dir=uploads
        app.admin.email=admin@jobportal.com
        app.admin.password=CHOOSE_A_PASSWORD

3. Start the backend:

        cd backend
        ./mvnw spring-boot:run

4. Start the frontend:

        cd frontend
        npm install
        npm run dev

5. Open http://localhost:5173 and Swagger at http://localhost:8080/swagger-ui.html

## Tests

    cd backend
    ./mvnw test

## Project structure

    backend/   Spring Boot API (controller, service, repository, entity, dto, security)
    frontend/  React app (pages, components, auth context)

## Screenshots

Add screenshots of the jobs page, recruiter dashboard, admin dashboard and Swagger UI here.

## Author

YOUR_NAME - https://github.com/YOUR_USERNAME