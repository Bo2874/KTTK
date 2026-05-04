-- Step 1 (Big Bang): reset schema theo logic nhiều bàn mỗi đơn
-- Engine: MySQL 8+
-- Booking status lưu trực tiếp trong tbl_booking_order.status (VARCHAR)
-- DishStat là DTO thống kê, KHÔNG tạo bảng vật lý trong DB
-- Trạng thái bàn theo khung giờ được SUY RA từ booking đã xác nhận

SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE restaurant_db;
SET FOREIGN_KEY_CHECKS = 0;

-- Drop theo thứ tự dependency
DROP TABLE IF EXISTS tbl_order_detail;
DROP TABLE IF EXISTS tbl_booking_order_table;
DROP TABLE IF EXISTS tbl_booking_order;

-- Drop các bảng cũ của module đặt bàn online
DROP TABLE IF EXISTS tbl_online_booking_tables;
DROP TABLE IF EXISTS tbl_online_booking;

DROP TABLE IF EXISTS tbl_dish;
DROP TABLE IF EXISTS tbl_restaurant_table;
DROP TABLE IF EXISTS tbl_employee;
DROP TABLE IF EXISTS tbl_customer;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE tbl_employee (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_code VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_employee_employee_code (employee_code),
    UNIQUE KEY uk_tbl_employee_email (email),
    UNIQUE KEY uk_tbl_employee_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_customer (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_customer_phone (phone),
    UNIQUE KEY uk_tbl_customer_email (email),
    UNIQUE KEY uk_tbl_customer_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_restaurant_table (
    id BIGINT NOT NULL AUTO_INCREMENT,
    table_code VARCHAR(255) NOT NULL,
    area VARCHAR(255) NOT NULL,
    capacity BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_restaurant_table_table_code (table_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_dish (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dish_code VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price DECIMAL(19,0) NOT NULL,
    is_available TINYINT(1) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_dish_dish_code (dish_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_booking_order (
    id BIGINT NOT NULL AUTO_INCREMENT,
    order_code VARCHAR(255) NOT NULL,
    booking_date DATE NOT NULL,
    arrival_time TIME NOT NULL,
    end_time TIME NOT NULL,
    total_amount DECIMAL(19,0) NOT NULL,
    status VARCHAR(255) NOT NULL,
    note VARCHAR(255) NULL,
    customer_id BIGINT NOT NULL,
    employee_id BIGINT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_booking_order_order_code (order_code),
    KEY idx_tbl_booking_order_customer (customer_id),
    KEY idx_tbl_booking_order_employee (employee_id),
    KEY idx_tbl_booking_order_time_status (booking_date, arrival_time, end_time, status),
    CONSTRAINT fk_booking_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES tbl_customer (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_booking_order_employee
        FOREIGN KEY (employee_id)
        REFERENCES tbl_employee (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_booking_order_table (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tbl_booking_order_id BIGINT NOT NULL,
    tbl_restaurant_table_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tbl_booking_order_table_pair (tbl_booking_order_id, tbl_restaurant_table_id),
    KEY idx_tbl_booking_order_table_booking (tbl_booking_order_id),
    KEY idx_tbl_booking_order_table_table (tbl_restaurant_table_id),
    CONSTRAINT fk_booking_order_table_booking
        FOREIGN KEY (tbl_booking_order_id)
        REFERENCES tbl_booking_order (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_booking_order_table_table
        FOREIGN KEY (tbl_restaurant_table_id)
        REFERENCES tbl_restaurant_table (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tbl_order_detail (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quantity INT NOT NULL,
    unit_price DECIMAL(19,0) NOT NULL,
    subtotal DECIMAL(19,0) NOT NULL,
    booking_order_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_tbl_order_detail_booking (booking_order_id),
    KEY idx_tbl_order_detail_dish (dish_id),
    CONSTRAINT fk_order_detail_booking
        FOREIGN KEY (booking_order_id)
        REFERENCES tbl_booking_order (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_order_detail_dish
        FOREIGN KEY (dish_id)
        REFERENCES tbl_dish (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verify created tables/columns
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'restaurant_db' AND table_name LIKE 'tbl_%'
ORDER BY table_name;

DESCRIBE tbl_booking_order;
DESCRIBE tbl_booking_order_table;
