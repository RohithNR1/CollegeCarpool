-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS college_carpool_db;
USE college_carpool_db;

-- Step 2: User Profiles Table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL
);

-- Step 3: Trips Table
CREATE TABLE IF NOT EXISTS trips (
    trip_id INT AUTO_INCREMENT PRIMARY KEY,
    driver_id INT,
    origin VARCHAR(100),
    destination VARCHAR(100),
    trip_date DATE,
    trip_time TIME,
    seats_available INT,
    fare_per_seat DECIMAL(10, 2),
    FOREIGN KEY (driver_id) REFERENCES users(user_id)
);

-- Step 4: Bookings Table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT,
    passenger_id INT,
    seats_booked INT,
    total_fare DECIMAL(10, 2),
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id),
    FOREIGN KEY (passenger_id) REFERENCES users(user_id)
);

-- Step 5: Ride History Table
CREATE TABLE IF NOT EXISTS ride_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    trip_id INT,
    role ENUM('driver', 'passenger'),
    status ENUM('completed', 'cancelled'),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (trip_id) REFERENCES trips(trip_id)
);