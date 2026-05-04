-- Bước 3: thêm tài khoản đăng nhập mẫu
USE restaurant_db;

-- Tài khoản khách hàng
INSERT INTO tbl_customer (full_name, phone, email, username, password)
VALUES
    ('Khách Hàng 01', '0900000001', 'khach01@example.com', 'khachhang', '123456'),
    ('Khách Hàng 02', '0900000002', 'khach02@example.com', 'khachhang2', '123456')
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password = VALUES(password);

-- Tài khoản nhân viên
INSERT INTO tbl_employee (employee_code, full_name, phone, email, username, password, role)
VALUES
    ('EMP001', 'Lễ Tân 01', '0910000001', 'letan01@example.com', 'letan', '123456', 'RECEPTIONIST'),
    ('EMP002', 'Quản Lý 01', '0910000002', 'quanly01@example.com', 'quanly', '123456', 'MANAGER')
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password = VALUES(password),
    role = VALUES(role);

SELECT id, username, full_name, phone FROM tbl_customer ORDER BY id;
SELECT id, employee_code, username, role FROM tbl_employee ORDER BY id;
