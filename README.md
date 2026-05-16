# 🏰 My Kingdom Backend — Setup & Deployment Guide

## ✅ Default Login Credentials
| Role    | Username  | Password      |
|---------|-----------|---------------|
| Admin   | admin     | Admin@123     |
| Teacher | teacher1  | Teacher@123   |

---

## 🖥️ Run Locally (IntelliJ IDEA)

### 1. Create MySQL Database
Open MySQL Workbench and run:
```sql
CREATE DATABASE IF NOT EXISTS mykingdom_db;
```
Then run the full `src/main/resources/db/V1__init_schema.sql` file.

### 2. Set Environment Variables in IntelliJ
Edit Configurations → Environment Variables:
```
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mykingdom_db
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=MyKingdomSuperSecretKey2024@Tirunelveli@SpringBoot
```

### 3. Run
Press Shift+F10. App starts at: http://localhost:8080

---

## 🚀 Deploy on Render

### Step 1 — Push to GitHub
```bash
git init
git add .
git commit -m "My Kingdom backend"
git remote add origin https://github.com/YOUR_USERNAME/mykingdom-backend.git
git push -u origin main
```

### Step 2 — Create Free MySQL on Aiven
1. Go to https://aiven.io → Sign up free
2. Create Service → MySQL → Free plan
3. Copy: Host, Port, Username, Password
4. Create database named: mykingdom_db
5. Run V1__init_schema.sql in Aiven Query Editor

### Step 3 — Deploy on Render
1. Go to https://render.com → New → Web Service
2. Connect your GitHub repo
3. Settings:
   - Runtime: Docker
   - Instance: Free
4. Add Environment Variables:
   - DB_HOST = your-aiven-host
   - DB_PORT = your-aiven-port
   - DB_NAME = mykingdom_db
   - DB_USERNAME = avnadmin
   - DB_PASSWORD = your-aiven-password
   - JWT_SECRET = MyKingdomSuperSecretKey2024@Tirunelveli@SpringBoot
   - CORS_ORIGINS = https://myschool-delta.vercel.app
5. Click Create Web Service

### Step 4 — Test Your Live API
```
POST https://mykingdom-backend.onrender.com/api/auth/login
Body: { "username": "admin", "password": "Admin@123" }
```

---

## 📡 API Endpoints

| Method | URL                          | Role     |
|--------|------------------------------|----------|
| POST   | /api/auth/login              | Public   |
| POST   | /api/auth/refresh            | Public   |
| POST   | /api/auth/logout             | Any      |
| GET    | /api/fees/structure          | Admin    |
| POST   | /api/fees/structure          | Admin    |
| PUT    | /api/fees/structure/{id}     | Admin    |
| DELETE | /api/fees/structure/{id}     | Admin    |
| GET    | /api/fees/my                 | Student  |
| GET    | /api/fees/my/summary         | Student  |
| POST   | /api/fees/pay                | Admin    |
| GET    | /api/fees/overdue            | Admin    |
| GET    | /api/fees/receipt/{no}       | Any      |
