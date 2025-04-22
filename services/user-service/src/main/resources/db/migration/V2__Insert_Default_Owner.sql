-- Insert default owner account
-- Password: admin123
INSERT INTO users (
    firstname,
    lastname,
    email,
    phone,
    address,
    username,
    password,
    role,
    created_at,
    updated_at,
    user_type
) VALUES (
             'Admin',
             'Owner',
             'admin@restaurant.com',
             '1234567890',
             '123 Restaurant St',
             'admin',
             '$2a$12$2hBbIQR0AB8IeYDP2yOzOeR1tfIVyBxZRWEmS7QNSWRauNS0MDpZO',
             'OWNER',
             NOW(),
             NOW(),
             'EMPLOYEE'
         );

-- Get the ID of the inserted user
SET @owner_id = LAST_INSERT_ID();

-- Insert into employees table
INSERT INTO employees (
    id,
    date_of_birth,
    id_card_number
) VALUES (
             @owner_id,
             '1980-01-01',
             'OWNER123456'
         );