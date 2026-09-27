# Mobile Gaming Tournament Portal

A working Java web app: squad leaders register teams + players, admin schedules
matches and enters results, and a public leaderboard ranks squads by points.

Stack: **JSP** (pages) + **Servlets** (validation/logic) + **JDBC** (MySQL) + **Maven**
(runs via `tomcat7-maven-plugin`, matching your existing VS Code setup).

## 1. Prerequisites

- JDK 11+ (`java -version`)
- Maven 3.6+ (`mvn -version`)
- MySQL 8.x running locally

## 2. Database setup

```bash
mysql -u root -p < database_schema.sql
```

This creates the `tournament_portal` database, all tables, a default admin
account, and a few sample squads/matches so you have something to test against
immediately.

- **Admin login:** username `admin`, password `admin123`

## 3. Configure DB credentials

Edit `src/main/java/com/tournament/db/DBConnection.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/tournament_portal?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root";   // <- change this
```

## 4. Run it

```bash
mvn clean compile tomcat7:run
```

Then open: **http://localhost:8080/tournament-portal/**

(To change the port or path, edit the `tomcat7-maven-plugin` config in `pom.xml`.)

### Alternative: build a WAR and deploy to your own Tomcat

```bash
mvn clean package
cp target/tournament-portal.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```
Then visit `http://localhost:8080/tournament-portal/`.

## 5. How to test that it actually works

Walk through this checklist in order — each step depends on the last:

1. **Home page loads** — `http://localhost:8080/tournament-portal/`
   should show the landing page with nav links (Home / Leaderboard / Matches /
   Register Squad / Admin).

2. **Leaderboard shows seed data** — click *Leaderboard*. You should see 3
   sample squads (Nova Strikers, Shadow Reapers, Iron Wolves) already ranked
   by points from the seed data in `database_schema.sql`.

3. **Matches page shows seed data** — click *Matches*. You should see 2
   completed matches with scores and 1 scheduled match with no score.

4. **Register a new squad** — go to *Register Squad*, fill in a squad name,
   leader name, email, and at least 2 players (name + in-game ID). Submit.
   - Try submitting with only 1 player or a duplicate squad name first —
     you should get a red validation error box, and the page should NOT
     insert anything (confirms server-side validation is working, not just
     the HTML `required` attribute).
   - Then submit a valid entry — you should get a green success message
     with a new squad ID.
   - Go back to *Leaderboard* — your new squad should now appear with
     0/0/0.

5. **Admin login** — go to *Admin*, log in with `admin` / `admin123`.
   - Try a wrong password first — confirms the login check actually
     rejects bad credentials rather than always succeeding.

6. **Schedule a match** — on the admin dashboard, pick two squads (e.g.
   your new squad vs. Nova Strikers), pick a date/time, submit. Confirm
   it appears in the "Matches — Enter Results" table below as Scheduled,
   and also on the public *Matches* page.

7. **Enter a result** — next to your new scheduled match, enter a score
   (e.g. `2` and `1`) and click Save.
   - Confirm the match flips to Completed with the score shown.
   - Go to *Leaderboard* — the winning squad's Wins and Points should have
     gone up by 1 and 3; the losing squad's Losses and Points by 1 and 1.
   - Try clicking Save again on the *same* match (or resubmitting the form)
     — it should refuse (result already recorded), confirming a match can't
     be double-counted.

8. **Log out** — click *Log Out*, then try visiting
   `http://localhost:8080/tournament-portal/admin/dashboard` directly.
   You should be bounced back to the login page — confirms the admin pages
   are actually session-protected, not just hidden behind a nav link.

If all 8 steps behave as described, the registration → scheduling →
result-entry → leaderboard pipeline is genuinely working end to end, not
just rendering static pages.

## 6. Project structure

```
tournament-portal/
├── pom.xml
├── database_schema.sql
├── src/main/java/com/tournament/
│   ├── db/DBConnection.java        # JDBC connection
│   ├── model/                      # Squad, Player, Match POJOs
│   ├── dao/                        # SquadDAO, MatchDAO, AdminDAO — all SQL lives here
│   ├── util/PasswordUtil.java      # SHA-256 hashing
│   └── servlet/                    # RegisterServlet, LeaderboardServlet, MatchesServlet,
│                                    # AdminLoginServlet, AdminDashboardServlet,
│                                    # ScheduleMatchServlet, UpdateResultServlet
└── src/main/webapp/
    ├── index.jsp, register.jsp, leaderboard.jsp, matches.jsp, error.jsp
    ├── admin/login.jsp, admin/dashboard.jsp
    ├── WEB-INF/web.xml, WEB-INF/header.jspf
    └── css/style.css
```

## 7. What's deliberately NOT included

To keep this an honest, testable core rather than a wall of unverifiable
code, this build does **not** include: payment/Stripe integration, live
WebSocket score updates, or player invitation workflows — none of these
were part of your actual project scope (registration + leaderboard). If you
want any of them added, they can be built on top of this same DAO/servlet
structure.
