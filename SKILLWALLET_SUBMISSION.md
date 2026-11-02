# 🎓 SkillWallet Capstone Submission Guide & Checklist

## Project: AI-Powered Customer Support Ticketing System
**Student:** 2nd-Year BTech Computer Science & Engineering  

---

## 📋 Pre-Submission Checklist

Before submitting to the SkillWallet portal, ensure you have completed every item below:

- [x] **Source Code Completed:** All backend models, controllers, services, repositories, and frontend assets are implemented.
- [x] **Zero-Error Build:** Automated tests pass (`./mvnw test`) and JAR builds cleanly (`./mvnw package`).
- [x] **Documentation Complete:** 
  - `README.md` (Setup, tech stack, screenshots guide, credentials)
  - `PROJECT_REPORT.md` (Formal BTech academic report)
  - `TESTING.md` (10 test cases, manual verification, sample payloads)
  - `DEPLOYMENT.md` (Free cloud hosting instructions)
- [ ] **Git Repository Initialized & Pushed to GitHub** (Steps below)
- [ ] **Live Demo URL Generated** (Steps below)
- [ ] **Demo Video / Screen Recording** (Optional but highly recommended for top grade)

---

## 1. Initializing Git & Pushing to GitHub

Open your terminal and run the following commands:

```bash
# 1. Navigate to the project directory
cd /Users/sakshamraghuwanshi/.gemini/antigravity/scratch/ai-customer-support-ticketing

# 2. Initialize a new Git repository
git init

# 3. Create a .gitignore file (if not already created)
cat << 'EOF' > .gitignore
target/
*.class
*.jar
*.war
.DS_Store
.idea/
*.iml
.vscode/
*.log
EOF

# 4. Stage all files
git add .

# 5. Commit the files
git commit -m "feat: complete AI-powered customer support ticketing system capstone"

# 6. Create a new repository on GitHub:
# Go to https://github.com/new and create a public repository named:
# ai-customer-support-ticketing

# 7. Link your local repo to GitHub and push:
git branch -M main
git remote add origin https://github.com/<your-github-username>/ai-customer-support-ticketing.git
git push -u origin main
```

Your GitHub Repository URL will be:  
`https://github.com/<your-github-username>/ai-customer-support-ticketing`

---

## 2. Obtaining Your Live Demo URL

Choose either of the two free options:

### Method A: Render.com (Recommended Free Cloud Deployment)
1. Sign in to [Render.com](https://render.com) using your GitHub account.
2. Click **New +** → **Web Service**.
3. Select your repository `ai-customer-support-ticketing`.
4. Choose environment `Docker` (it will use the bundled `Dockerfile`).
5. Select the **Free** instance type and click **Deploy Web Service**.
6. Once deployed, copy your Live Demo URL:  
   `https://ai-customer-support-ticketing-xxxx.onrender.com`

### Method B: Cloudflare Quick Tunnel (Instant Free URL for Local Testing)
If presenting live or testing locally:
```bash
cloudflared tunnel --url http://localhost:8080
```
Copy the temporary public HTTPS URL provided in terminal output.

---

## 3. SkillWallet Submission Form Fields

When filling out the submission on SkillWallet:

| Submission Field | What to Provide |
|------------------|-----------------|
| **Project Title** | AI-Powered Customer Support Ticketing System |
| **Track / Domain** | Full-Stack Web Development & Applied AI |
| **GitHub Repository URL** | `https://github.com/<your-username>/ai-customer-support-ticketing` |
| **Live Demo URL** | `https://ai-customer-support-ticketing-xxxx.onrender.com` (or local tunnel URL) |
| **Core Technologies** | Java, Spring Boot 3, Spring Data JPA, H2 Database, MySQL, HTML5, CSS3, JavaScript, Chart.js, Google Gemini API |
| **Project Summary** | A full-featured enterprise support ticketing web app with a 5-stage Kanban board, Chart.js SLA analytics dashboard, dual-mode AI ticket auto-classification and response drafting (Gemini + Rule-Based Demo engine), audit history, and zero-config embedded H2 persistence. |
| **Attached Report** | Upload or link `PROJECT_REPORT.md` |

---

## 4. Viva Voce / Presentation Readiness Checklist

Be prepared to answer these common viva questions during your evaluation:

1. **Q: Why did you choose H2 over MySQL for default development?**  
   *A:* H2 is an embedded in-memory/file relational engine that eliminates external installation dependencies for evaluators, while Spring Data JPA abstraction allows effortless switching to MySQL in production via `application-mysql.properties`.

2. **Q: How does the AI classification work if no API key is provided?**  
   *A:* The architecture includes a robust fallback to a deterministic **Rule-Based NLP Demo Engine** that tokenizes text, matches keyword densities across 5 domains, evaluates urgency indicators, and computes a confidence score, ensuring 100% offline uptime.

3. **Q: How is the Kanban board state synchronized with the database?**  
   *A:* When a card is dragged or the advance arrow (`→`) is clicked, an asynchronous `PUT /api/tickets/{id}` REST request updates the ticket status in the database, automatically generates an audit comment in the ticket timeline, and triggers a real-time refresh of metrics and Chart.js charts.
