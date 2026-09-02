-- ============================================================
-- Campus Lost-and-Found Management System - Database Schema
-- Database: MySQL
-- ============================================================

-- Create Database if it does not exist
CREATE DATABASE IF NOT EXISTS campus_lost_found;
USE campus_lost_found;

-- Drop table if it exists (for clean resets)
DROP TABLE IF EXISTS items;

-- Create Items Table
CREATE TABLE items (
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

-- Insert Initial Sample Records for Testing
INSERT INTO items (item_name, description, category, status, location, date_reported, reported_by, contact)
VALUES 
('Blue Leather Wallet', 'Contains student ID card and driver license', 'Personal Effects', 'Lost', 'Main Library 2nd Floor', '2026-08-28', 'Alex Johnson', '555-0192'),
('Apple AirPods Pro', 'White wireless earbuds in a silicone case with sticker', 'Electronics', 'Found', 'Student Activity Center', '2026-08-29', 'Sarah Smith', '555-0143'),
('Calculus Textbook 10th Ed', 'Hardcover math book with yellow highlighter marks', 'Books & Stationery', 'Lost', 'Science Building Room 304', '2026-08-30', 'David Miller', '555-0188'),
('Stainless Water Bottle', 'Hydroflask 32oz dark blue with stickers', 'Personal Effects', 'Returned', 'Cafeteria Table 12', '2026-08-25', 'Emily Davis', '555-0176'),
('HP Laptop Charger', '65W USB-C power adapter found near outlet', 'Electronics', 'Found', 'Engineering Computer Lab B', '2026-08-31', 'Professor James', '555-0112');
