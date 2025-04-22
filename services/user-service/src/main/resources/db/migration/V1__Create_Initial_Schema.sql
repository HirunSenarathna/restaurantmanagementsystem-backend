CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       firstname VARCHAR(100) NOT NULL,
                       lastname VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       phone VARCHAR(20) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP,
                       user_type VARCHAR(20) NOT NULL
);

CREATE TABLE customers (
                           id BIGINT PRIMARY KEY,
                           FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE employees (
                           id BIGINT PRIMARY KEY,
                           date_of_birth DATE NOT NULL,
                           id_card_number VARCHAR(50) NOT NULL UNIQUE,
                           FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_employees_id_card_number ON employees(id_card_number);