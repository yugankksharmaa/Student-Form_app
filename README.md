# Student Form Application

A simple Java (Servlet + JSP) web application that collects student details
via a form and stores them in a PostgreSQL database. Built with Maven,
version-controlled on GitLab, deployed via Jenkins CI/CD to Apache Tomcat.

## Tech Stack
- Java Servlet + JSP
- Maven (build & WAR packaging)
- PostgreSQL (database)
- GitLab (source control)
- Jenkins (CI/CD)
- Apache Tomcat (deployment)

---

## 1. PostgreSQL Setup

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib -y
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Switch to postgres user and open psql
sudo -i -u postgres
psql
```

Inside `psql`, run the script in `sql/setup.sql`:

```sql
CREATE DATABASE "projectForm";
\c projectForm

CREATE TABLE formDetails (
    id                      SERIAL PRIMARY KEY,
    firstName               VARCHAR(50)  NOT NULL,
    lastName                VARCHAR(50)  NOT NULL,
    dob                     DATE         NOT NULL,
    gender                  VARCHAR(10)  NOT NULL,
    highestqualification    VARCHAR(100) NOT NULL,
    year_of_passing         INT          NOT NULL,
    mobilenumber            VARCHAR(10)  NOT NULL,
    created_at              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);
```

Set a password for the `postgres` user (matches `DBConnection.java`):

```sql
ALTER USER postgres PASSWORD 'postgres';
```

> **Update credentials:** If your DB user/password/host differ, edit
> `src/main/java/com/studentform/dao/DBConnection.java` before building.

---

## 2. Application Code

The app is a standard Maven WAR project:

```
student-form-app/
├── pom.xml
├── Jenkinsfile
├── sql/setup.sql
└── src/main/
    ├── java/com/studentform/
    │   ├── model/Student.java
    │   ├── dao/DBConnection.java
    │   ├── dao/StudentDAO.java
    │   └── servlet/SubmitServlet.java
    └── webapp/
        ├── index.jsp      (the form)
        ├── success.jsp
        ├── failure.jsp
        ├── css/style.css
        └── WEB-INF/web.xml
```

Build locally to verify it compiles:

```bash
mvn clean package
```

This produces `target/form.war` — the `finalName` in `pom.xml` is set to
`form`, so it deploys as context path `/form` (i.e. `http://<host>:8080/form`).

---

## 3. GitLab Setup

```bash
cd student-form-app
git init
git add .
git commit -m "Initial commit: Student Form Application"

# Create an empty project on GitLab first (via UI), then:
git remote add origin https://gitlab.com/<your-username>/student-form-app.git
git branch -M main
git push -u origin main
```

Going forward, commit meaningfully as you change things:

```bash
git add .
git commit -m "Fix: mobile number validation in SubmitServlet"
git push
```

---

## 4. Apache Tomcat Setup

```bash
sudo apt install openjdk-11-jdk -y

cd /opt
sudo wget https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.91/bin/apache-tomcat-9.0.91.tar.gz
sudo tar -xzf apache-tomcat-9.0.91.tar.gz
sudo mv apache-tomcat-9.0.91 tomcat
sudo chmod +x /opt/tomcat/bin/*.sh

# Start Tomcat
sudo /opt/tomcat/bin/startup.sh
```

Tomcat now runs on port `8080`. Once the WAR is deployed (next step),
the app is reachable at:

```
http://<your_server_ip_or_name>:8080/form
```

> The task's example URL `http://<your_name>/form` assumes Tomcat is either
> reverse-proxied on port 80 under a hostname, or you access it directly as
> `http://<host>:8080/form`. If you need port 80 without `:8080`, put Nginx/Apache
> HTTPD in front of Tomcat as a reverse proxy, or change Tomcat's connector
> port in `conf/server.xml` to `80` (requires root privileges).

Also drop the PostgreSQL JDBC driver into Tomcat's `lib/` folder (in addition
to being bundled in the WAR, this avoids classloader issues on some setups):

```bash
sudo cp ~/.m2/repository/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar /opt/tomcat/lib/
```

---

## 5. Jenkins Setup & Pipeline

```bash
sudo apt install openjdk-11-jdk -y
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt-get update
sudo apt-get install jenkins -y
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

Jenkins runs on port `8080` by default — if that clashes with Tomcat,
change Jenkins' port in `/etc/default/jenkins` (or run Tomcat on a different
port).

### Configure Jenkins
1. Unlock Jenkins (`/var/lib/jenkins/secrets/initialAdminPassword`), install
   suggested plugins.
2. Install plugins: **Git**, **Maven Integration**, **Pipeline**.
3. **Manage Jenkins → Tools**:
   - Add a Maven installation named `Maven3`.
   - Add a JDK installation named `JDK11`.
4. **Manage Jenkins → Credentials**: add your GitLab username/token as
   credentials with ID `gitlab-credentials`.
5. **New Item → Pipeline**, point it at this repo, and select
   "Pipeline script from SCM" → Git → your GitLab URL → `Jenkinsfile`.

### The Pipeline (`Jenkinsfile`)
Already included in the repo. It:
1. Checks out code from GitLab.
2. Runs `mvn clean package` to build the WAR.
3. Archives the WAR as a build artifact.
4. Copies the WAR into Tomcat's `webapps/` folder to deploy it.

Make sure the Jenkins service user has write access to
`/opt/tomcat/webapps/`:

```bash
sudo chown -R jenkins:jenkins /opt/tomcat/webapps
```

Every time you push a new commit to GitLab, trigger the Jenkins job
(manually, or set up a GitLab webhook for auto-trigger) and it rebuilds +
redeploys automatically.

---

## 6. Verify End-to-End

1. Open `http://<host>:8080/form`.
2. Fill the form and submit.
3. Check success/failure message on screen.
4. Confirm the row landed in the DB:

```bash
psql -U postgres -d projectForm -c "SELECT * FROM formDetails;"
```

---

## Notes
- Update DB credentials in `DBConnection.java` to match your environment
  before building.
- `pattern="[0-9]{10}"` enforces a 10-digit mobile number both client-side
  (HTML5) and server-side (`SubmitServlet`).
- All submitted data is validated server-side even if JS/HTML5 validation
  is bypassed.
