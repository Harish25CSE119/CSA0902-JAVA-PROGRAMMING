# Campus Lost-and-Found Management System

A multi-platform system featuring a **Localhost Web Application** and an upgraded **Java Swing Desktop Application** paired with **JDBC** and **MySQL** for managing lost, found, and returned items across a college campus.

---

## 🌟 What's New & Upgraded

1. **Localhost Web Application (`http://localhost:5000`)**:
   - Built with Node.js, Express REST APIs, and a modern responsive web frontend.
   - Live dashboard cards for total, lost, found, and returned items.
   - Multi-criteria real-time search & filter (keyword, category, status, location).
   - Glassmorphic interface with **Dark / Light theme toggle**.
   - Automatic dual-mode database storage: connects directly to MySQL database `campus_lost_found` when online, or runs seamlessly using an interactive in-memory data store when MySQL is offline.

2. **Java Swing Desktop Application UI Overhaul**:
   - Fixed all font and background color collisions across all screens (`LoginFrame`, `DashboardFrame`, `LostItemFrame`, `FoundItemFrame`, `ViewItemsFrame`, `SearchItemFrame`, `UpdateItemFrame`, `DeleteItemFrame`, `ReturnItemFrame`).
   - Explicitly assigned high-contrast foreground (`COLOR_TEXT_MAIN`) and surface backgrounds (`COLOR_SURFACE`) to text fields, combo boxes, text areas, labels, buttons, and JTables.
   - Added custom JComboBox cell renderers and JTable badge color highlights for crystal-clear readability under all system Look & Feels.

---

## 📁 Project Directory Structure

```
c:\CAMPUS LOST AND FOUND MANAGEMENT SYSTEM\
│
├── schema.sql                         # MySQL Database & Table Creation Script
├── build_and_run.bat                  # Batch script to compile and launch Desktop Application
├── run_web.bat                        # Batch script to start Web Server & launch localhost in browser
│
├── server.js                          # Express REST Server for Web Application
├── package.json                       # Node.js dependencies & scripts
│
├── public/                            # Web Application Frontend Assets
│   ├── index.html                     # Responsive Web UI
│   ├── style.css                      # Modern CSS token design system (Dark & Light theme)
│   └── app.js                         # REST API integration, modals, filters & toast alerts
│
├── lib/
│   └── mysql-connector-j-8.3.0.jar    # MySQL JDBC Connector Driver for Java
│
├── src/
│   ├── Main.java                      # Desktop Application Entry Point
│   ├── database/
│   │   └── DatabaseConnection.java    # Centralized JDBC connection manager
│   ├── model/
│   │   └── Item.java                  # Domain entity POJO
│   ├── dao/
│   │   └── ItemDAO.java               # Data Access Object with SQL CRUD logic
│   └── gui/
│       ├── UIUtils.java               # High-contrast UI Design System & Component Customizers
│       ├── LoginFrame.java            # Admin login screen
│       ├── DashboardFrame.java        # Main dashboard with live statistics & module navigation
│       ├── LostItemFrame.java         # Form for reporting lost items
│       ├── FoundItemFrame.java        # Form for reporting found items
│       ├── ViewItemsFrame.java        # Read-only JTable view of all records
│       ├── SearchItemFrame.java       # Multi-criteria search filter screen
│       ├── UpdateItemFrame.java       # Edit record interface
│       ├── DeleteItemFrame.java       # Record deletion interface with preview & confirmation
│       └── ReturnItemFrame.java       # Status updater (marking items as Returned)
│
└── bin/                               # Compiled Java Bytecode (.class files)
```

---

## 🚀 How to Run the Web Application on Localhost

### Option 1: Using Windows Batch Script (Recommended)
Double-click `run_web.bat` or run from terminal:
```cmd
run_web.bat
```
This automatically installs npm dependencies if needed, launches the Express web server, and opens your default browser at:
👉 **`http://localhost:5000`**

### Option 2: Manual Terminal Execution
```cmd
npm install
npm start
```
Then open `http://localhost:5000` in any web browser.

---

## 💻 How to Run the Java Swing Desktop Application

### Option 1: Using Windows Batch Script
Double-click `build_and_run.bat` or run:
```cmd
build_and_run.bat
```

### Option 2: Manual Command Line Execution
1. **Compile Source Code**:
   ```cmd
   javac -cp "lib/mysql-connector-j-8.3.0.jar" -d bin src/database/*.java src/model/*.java src/dao/*.java src/gui/*.java src/Main.java
   ```
2. **Run Desktop Application**:
   ```cmd
   java -cp "bin;lib/mysql-connector-j-8.3.0.jar" Main
   ```

---

## 🔑 Predefined Credentials

- **Username**: `admin`
- **Password**: `admin123`

---

## 🌐 Web REST API Reference

| HTTP Method | Endpoint | Description |
|---|---|---|
| **GET** | `/api/stats` | Summary counts (`total`, `lost`, `found`, `returned`) |
| **GET** | `/api/items` | Query items (supports `keyword`, `category`, `status`, `location` query params) |
| **GET** | `/api/items/:id` | Fetch item details by ID |
| **POST** | `/api/items` | Report new Lost or Found item |
| **PUT** | `/api/items/:id` | Update existing item record |
| **PUT** | `/api/items/:id/return` | Mark item status as `Returned` |
| **DELETE** | `/api/items/:id` | Delete item record permanently |

---

## 🗄️ MySQL Database Setup (Optional for Web, Required for Java JDBC)

```sql
CREATE DATABASE IF NOT EXISTS campus_lost_found;
USE campus_lost_found;

CREATE TABLE IF NOT EXISTS items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL, -- Values: 'Lost', 'Found', 'Returned'
    location VARCHAR(150) NOT NULL,
    date_reported DATE NOT NULL,
    reported_by VARCHAR(100) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```
