# 🚀 Cloud Deployment & Live Demo Hosting Guide

## AI-Powered Customer Support Ticketing System

This guide outlines feasible free-tier hosting solutions to deploy the application online and obtain a public live demo URL for your SkillWallet submission.

---

## Option 1: Render.com (Recommended Free Cloud Hosting)

Render provides free hosting for containerized or web applications with automated GitHub continuous deployment.

### Steps:
1. **Push Code to GitHub:**
   Ensure your code is pushed to your personal GitHub repository (see [SKILLWALLET_SUBMISSION.md](file:///Users/sakshamraghuwanshi/.gemini/antigravity/scratch/ai-customer-support-ticketing/SKILLWALLET_SUBMISSION.md)).
2. **Sign Up on Render:**
   Navigate to [https://render.com](https://render.com) and sign in using your GitHub account.
3. **Create New Web Service:**
   - Click **New +** → **Web Service**.
   - Select your GitHub repository: `ai-customer-support-ticketing`.
4. **Configure Settings:**
   - **Name:** `ai-support-desk-<your-name>`
   - **Language:** `Docker` (Render will automatically detect the included `Dockerfile`).
   - **Region:** Closest to your location (e.g., Singapore or Frankfurt).
   - **Instance Type:** `Free`.
5. **Environment Variables (Optional):**
   - If using Google Gemini API: Add `GEMINI_API_KEY` with your secret key.
   - If omitted, the app runs in **High-Accuracy Rule-Based Demo Mode** with 0 cloud dependencies!
6. **Deploy:**
   - Click **Deploy Web Service**.
   - Render will build the Docker container and provide a live public URL:  
     `https://ai-support-desk-xxxx.onrender.com`.

---

## Option 2: Railway.app (Fast 1-Click Deployment)

Railway offers free starter trial credits and native Maven/Java auto-detection.

### Steps:
1. Go to [https://railway.app](https://railway.app) and sign in with GitHub.
2. Click **New Project** → **Deploy from GitHub repo**.
3. Select `ai-customer-support-ticketing`.
4. Railway will automatically detect the Maven project, run `./mvnw clean package`, and deploy the resulting JAR.
5. In project **Settings** → **Networking**, click **Generate Domain** to get your public live URL:  
   `https://ai-customer-support-ticketing-production-xxxx.up.railway.app`.

---

## Option 3: Instant Live URL via Cloudflare Tunnel / Ngrok (Zero Cloud Cost)

If you wish to demo your local machine's running instance directly to examiners:

### Using Cloudflare Quick Tunnels (100% Free & No Account Needed):
```bash
# If cloudflared is installed:
cloudflared tunnel --url http://localhost:8080
```
This generates an instant HTTPS public URL (e.g. `https://random-words.trycloudflare.com`) pointing directly to your local Spring Boot instance.

### Using Ngrok:
```bash
ngrok http 8080
```
Provides a public HTTPS URL forwarding requests to `localhost:8080`.
