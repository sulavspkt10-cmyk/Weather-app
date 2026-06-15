# Weather App

A full-stack weather dashboard built with Java Spark on the backend and React on the frontend. Public users can look up current weather conditions for any city. Admins can log in to manage a user database.

---

## Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spark Java 2.9.4 |
| Database | MongoDB Atlas |
| Frontend | React 18, React Router 6, Vite 6 |
| Auth | BCrypt password hashing, server-side sessions |
| Weather data | OpenWeatherMap API |

---

## Project structure

```
weather-app/
├── frontend/               React source (Vite project)
│   ├── src/
│   │   ├── pages/          Home, Login, Dashboard, Users
│   │   ├── components/     Navbar, ProtectedRoute
│   │   ├── api.js          All fetch calls in one place
│   │   └── index.css       CSS custom properties / theme
│   ├── vite.config.js
│   └── package.json
└── src/main/
    ├── java/com/weatherapp/
    │   ├── App.java                Routes and server setup
    │   ├── config/                 MongoDB connection
    │   ├── middleware/             Auth middleware
    │   ├── models/                 User, Admin models
    │   └── services/               Auth, User, Weather services
    └── resources/
        └── config.properties       Runtime config (not committed)
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 20.10+ (only needed if building the frontend manually)
- A MongoDB Atlas cluster
- An OpenWeatherMap API key (free tier works)

---

## Setup

**1. Clone the repository**

```bash
git clone https://github.com/sulavspkt10-cmyk/Weather-app.git
cd Weather-app
```

**2. Create your config file**

```bash
cp src/main/resources/config.properties.example src/main/resources/config.properties
```

Open `config.properties` and fill in your values:

```properties
MONGO_URI=mongodb+srv://<user>:<password>@<cluster>.mongodb.net/weatherapp?retryWrites=true&w=majority
DB_NAME=weatherapp
WEATHER_API_KEY=<your-openweathermap-key>
PORT=4567
```

**3. Register the first admin**

Before starting the server, register your admin account. Once one admin exists the `/setup` endpoint locks itself permanently.

```bash
curl -X POST "http://localhost:4567/setup" \
  -d "name=Your Name" \
  -d "email=you@example.com" \
  -d "password=yourpassword"
```

Run this after the server starts (see step 4).

---

## Running the app

### Option A — single command (recommended)

`mvn package` builds the React frontend and compiles the Java backend in one step. Node.js is downloaded automatically by the Maven plugin; no global install required.

```bash
mvn package
java -jar target/weather-app-1.0-SNAPSHOT.jar
```

Open `http://localhost:4567`.

### Option B — separate dev servers

Run the Java backend and the Vite dev server independently. Useful when iterating on the frontend because Vite gives instant hot reload.

```bash
# Terminal 1 — Java backend (port 4567)
mvn compile exec:java -Dexec.mainClass=com.weatherapp.App

# Terminal 2 — Vite dev server (port 3000, proxies API to 4567)
cd frontend
npm install
npm run dev
```

Open `http://localhost:3000`.

---

## Building the frontend only

```bash
cd frontend
npm run build
```

Output goes to `src/main/resources/public/` and gets picked up the next time you compile the Java project.

---

## Routes

### Public

| Method | Path | Description |
|---|---|---|
| GET | `/` | Home page — weather search |
| GET | `/weather?city=<name>` | Weather JSON for a city |
| POST | `/login` | Admin login, returns JSON |
| GET | `/api/auth/status` | Check if session is active |

### Admin (requires valid session cookie)

| Method | Path | Description |
|---|---|---|
| GET | `/dashboard` | Admin dashboard |
| GET | `/users` | Users list |
| GET | `/api/users` | Users as JSON |
| POST | `/api/save-user` | Create a user (JSON body) |
| DELETE | `/api/user/:id` | Delete a user by MongoDB ID |
| POST | `/logout` | Invalidate session |

### One-time setup

| Method | Path | Description |
|---|---|---|
| POST | `/setup` | Register first admin — returns 403 once an admin exists |

---

## Security

- Passwords are hashed with BCrypt before storage. Plain-text passwords are never written to the database or logs.
- Session tokens are stored in an `HttpOnly; SameSite=Strict` cookie so they are not accessible from JavaScript and are not sent on cross-origin requests.
- Sessions expire after one hour. Logout deletes the session from MongoDB and immediately expires the cookie.
- The `/setup` endpoint checks `admins.countDocuments()` on every request and returns 403 if any admin exists, so it cannot be used to create additional accounts.
- All responses include `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`, `Content-Security-Policy`, and `Referrer-Policy` headers.
- User input (name, city, address) is length-validated server-side. MongoDB ObjectId parameters are validated against a 24-character hex pattern before hitting the database.
- The login endpoint runs BCrypt against a dummy hash when the email is not found, so response time is consistent regardless of whether the email exists. This prevents email enumeration via timing.

---

## Environment variables

`config.properties` is gitignored and must never be committed. Use `config.properties.example` as the template. If you accidentally commit credentials, rotate them immediately — the file history remains readable even after the file is removed.

---

## License

MIT
