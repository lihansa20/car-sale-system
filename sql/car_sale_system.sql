
DROP DATABASE IF EXISTS car_sale_system;
CREATE DATABASE car_sale_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_sale_system;

-- USERS
CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(64)  NOT NULL,          -- SHA-256 hash
    full_name   VARCHAR(100) NOT NULL,
    role        ENUM('ADMIN','SALESPERSON') NOT NULL DEFAULT 'SALESPERSON',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CUSTOMERS
CREATE TABLE customers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    nic         VARCHAR(20)  NOT NULL UNIQUE,
    phone       VARCHAR(15)  NOT NULL,
    email       VARCHAR(100),
    address     VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CARS
CREATE TABLE cars (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    brand          VARCHAR(50)  NOT NULL,
    model          VARCHAR(50)  NOT NULL,
    year           INT          NOT NULL,
    color          VARCHAR(30),
    fuel_type      ENUM('Petrol','Diesel','Hybrid','Electric') NOT NULL,
    cost_price     DECIMAL(12,2) NOT NULL CHECK (cost_price > 0),
    selling_price  DECIMAL(12,2) NOT NULL CHECK (selling_price > 0),
    status         ENUM('Available','Sold') NOT NULL DEFAULT 'Available',
    added_date     DATE NOT NULL
);

-- SALES
CREATE TABLE sales (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    car_id       INT NOT NULL UNIQUE,
    customer_id  INT NOT NULL,
    user_id      INT NOT NULL,
    sale_date    DATE NOT NULL,
    sale_price   DECIMAL(12,2) NOT NULL CHECK (sale_price > 0),
    profit       DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (car_id)      REFERENCES cars(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (user_id)     REFERENCES users(id)
);



INSERT INTO users (username, password, full_name, role) VALUES
('admin',  SHA2('admin123', 256), 'System Admin',  'ADMIN'),
('nimal',  SHA2('sales123', 256), 'Nimal Perera',  'SALESPERSON');

INSERT INTO customers (name, nic, phone, email, address) VALUES
('Kamal Silva',     '199012345678', '0771234567', 'kamal@mail.com',   'Negombo'),
('Sanduni Fernando','199545678901', '0712345678', 'sanduni@mail.com', 'Colombo'),
('Ruwan Jayasinghe','198834567890', '0759876543', 'ruwan@mail.com',   'Gampaha');

INSERT INTO cars (brand, model, year, color, fuel_type, cost_price, selling_price, status, added_date) VALUES
('Toyota', 'Axio',    2018, 'White',  'Petrol',   6500000.00,  7200000.00, 'Sold',      '2026-07-10'),
('Honda',  'Vezel',   2017, 'Black',  'Hybrid',   9000000.00, 10000000.00, 'Sold',      '2026-07-15'),
('Suzuki', 'Wagon R', 2019, 'Silver', 'Petrol',   4200000.00,  4700000.00, 'Available', '2026-08-01'),
('Nissan', 'Leaf',    2016, 'Blue',   'Electric', 5800000.00,  6500000.00, 'Available', '2026-08-05'),
('Toyota', 'Aqua',    2015, 'Red',    'Hybrid',   5000000.00,  5600000.00, 'Available', '2026-08-20'),
('BMW',    '320i',    2014, 'Grey',   'Petrol',  12000000.00, 13500000.00, 'Sold',      '2026-08-25');

INSERT INTO sales (car_id, customer_id, user_id, sale_date, sale_price, profit) VALUES
(1, 1, 2, '2026-08-15',  7100000.00,   600000.00),
(2, 2, 2, '2026-09-02',  9900000.00,   900000.00),
(6, 3, 1, '2026-09-20', 13200000.00,  1200000.00);