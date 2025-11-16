-- Drop tables if they exist (for clean restart)
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS manicure_service;
DROP TABLE IF EXISTS users;

-- Create manicure_service table
CREATE TABLE manicure_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL
);

-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Create appointment table
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(255),
    customer_phone VARCHAR(20),
    start_at TIMESTAMP NOT NULL,
    note TEXT,
    price_at_booking DECIMAL(10,2),
    discount_percent INT DEFAULT 0,
    referral_code VARCHAR(50),
    referred_by VARCHAR(50),
    FOREIGN KEY (service_id) REFERENCES manicure_service(id)
);

-- Create product table
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    stock INT DEFAULT 0
);

-- Create indexes for better performance
CREATE INDEX idx_appointment_start_at ON appointment(start_at);
CREATE INDEX idx_appointment_customer_email ON appointment(customer_email);
CREATE INDEX idx_appointment_customer_phone ON appointment(customer_phone);
