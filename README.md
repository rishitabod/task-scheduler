# TaskScheduler

A personal task manager that always knows what to do next. Tasks are ranked by priority and deadline using a priority queue, and a **Smart Time Planner** picks the best tasks that fit into the free time you have.

**Live demo:** https://task-scheduler-s1o9.onrender.com

> The demo is hosted on a free plan, so the first load after a quiet period can take a few seconds while the server wakes up.

<!-- Add a screenshot: save it as docs/screenshot.png and uncomment the next line -->
<!-- ![TaskScheduler dashboard](docs/screenshot.png) -->

## Features

- **Always know what's next:** an "Up next" banner shows the most important unfinished task.
- **Priority queue ordering:** tasks are sorted by priority (High, Medium, Low), then by deadline.
- **Smart Time Planner:** enter the time you have free, and it suggests the tasks you can finish in it.
- **Deadline tracking:** overdue tasks are flagged automatically.
- **Progress bar and stats:** see total, high priority, medium priority, and completed tasks at a glance.
- **Search and filters:** find tasks by name, or filter by priority, done, or overdue.
- **Accounts:** register and log in. Every user sees only their own tasks.
- **Responsive design:** the layout adapts to phones, tablets, and laptops.

## Tech stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.2, Spring Web, Spring Data JPA (Hibernate) |
| Database | MySQL |
| Auth | HTTP sessions, passwords hashed with BCrypt (`spring-security-crypto`) |
| Frontend | HTML, CSS, and vanilla JavaScript (no framework), Lucide icons |
| Testing | JUnit 5, Mockito |
| Deployment | Docker, Render (app), Aiven (MySQL) |

## How the scheduling works

- **Ordering:** tasks go into a `PriorityQueue` with a comparator on priority first, then deadline. Polling the queue returns them in the order you should do them.
- **Smart Time Planner:** a greedy algorithm that takes the unfinished tasks with an estimated time, walks through them in priority order, and adds each one that still fits in the remaining minutes.

## API

All task endpoints require a logged-in session.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account |
| POST | `/api/auth/login` | Log in |
| POST | `/api/auth/logout` | Log out |
| GET | `/api/auth/session` | Check whether you're logged in |
| GET | `/api/tasks` | List your tasks, sorted |
| POST | `/api/tasks` | Add a task |
| PUT | `/api/tasks/{id}` | Edit a task |
| PUT | `/api/tasks/{id}/complete` | Mark a task done |
| DELETE | `/api/tasks/{id}` | Delete a task |
| GET | `/api/tasks/next` | Get the next task to do |
| GET | `/api/tasks/smart-schedule?minutes=N` | Get tasks that fit in N minutes |

## Run it locally

**You need:** Java 21, Maven, and MySQL running on port 3306.

1. Clone the repo:
   ```
   git clone https://github.com/rishitabod/task-scheduler.git
   cd task-scheduler
   ```
2. Set your MySQL password as an environment variable (PowerShell):
   ```
   $env:DB_PASSWORD = "your-mysql-password"
   ```
   macOS or Linux: `export DB_PASSWORD="your-mysql-password"`
3. Start the app:
   ```
   mvn spring-boot:run
   ```
4. Open http://localhost:8080

The app creates the `taskscheduler` database and its tables on first run.

### Configuration

All settings are read from environment variables, so no secrets live in the repo.

| Variable | Default | Purpose |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/taskscheduler?createDatabaseIfNotExist=true` | Database address |
| `DB_USERNAME` | `root` | Database user |
| `DB_PASSWORD` | none (required) | Database password |
| `PORT` | `8080` | Port the server listens on |

## Run the tests

```
mvn test
```

The unit tests cover the scheduler logic with JUnit 5 and Mockito.

## Deployment

The repo includes a `Dockerfile` (a multi-stage build with Maven, then a slim Java 21 runtime). To deploy:

1. Create a MySQL database (the demo uses Aiven's free plan).
2. Create a Docker web service on your host (the demo uses Render) from this repo.
3. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the host's environment settings. For a hosted database, the URL looks like `jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED`.

## Project structure

```
src/main/java/com/taskscheduler/
  TaskSchedulerApp.java        Application entry point
  TaskController.java          Task REST endpoints
  UserController.java          Register, login, logout, session
  PriorityQueueScheduler.java  Scheduling logic (priority queue, smart planner)
  Task.java, User.java         JPA entities
  TaskRepository.java, UserRepository.java
src/main/resources/
  application.properties       Settings read from environment variables
  static/index.html            Single-page frontend
  static/cherry.css            Styles and responsive layout
src/test/java/                 Unit tests
Dockerfile                     Container build
```

## Security notes

- Passwords are hashed with BCrypt and never stored in plain text.
- Users can only view, edit, complete, or delete their own tasks. Each change is checked against the task's owner.
- Database credentials come from environment variables and are never committed.

## Author

Built by **Rishitabodh Upadhyaya**.
