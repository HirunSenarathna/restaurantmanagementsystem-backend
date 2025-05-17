
-- Orders Table
CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        customer_id BIGINT,
                        customer_name VARCHAR(255),
                        error_message TEXT,
                        is_paid BOOLEAN,
                        order_status VARCHAR(50),
                        order_time DATETIME,
                        payment_id BIGINT,
                        payment_link LONGTEXT,
                        payment_method VARCHAR(100),
                        payment_status VARCHAR(100),
                        special_instructions TEXT,
                        table_number INT,
                        total_amount DECIMAL(19,2),
                        transaction_id VARCHAR(255),
                        updated_at DATETIME,
                        waiter_id BIGINT,
                        waiter_name VARCHAR(255)
);

-- Order Items Table
CREATE TABLE order_items (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             created_at DATETIME,
                             menu_item_id BIGINT,
                             menu_item_name VARCHAR(255),
                             menu_item_variant_id BIGINT,
                             order_id BIGINT,
                             quantity INT,
                             size VARCHAR(100),
                             special_instructions TEXT,
                             sub_total DECIMAL(19,2),
                             unit_price DECIMAL(19,2),
                             updated_at DATETIME,
                             variant VARCHAR(100),
                             FOREIGN KEY (order_id) REFERENCES orders(id)
);





-- -- Create orders table first since order_items references it
-- CREATE TABLE orders (
--                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                         customer_id BIGINT NOT NULL,
--                         customer_name VARCHAR(255) ,
--                         waiter_id BIGINT,
--                         waiter_name VARCHAR(255),
--                         table_number INT,
--                         order_status ENUM('PLACED', 'ACCEPTED', 'PREPARING', 'READY', 'DELIVERED', 'CANCELLED') NOT NULL,
--                         order_time DATETIME NOT NULL,
--                         estimated_delivery_time DATETIME,
--                         completion_time DATETIME,
--                         total_amount DECIMAL(19, 2) NOT NULL,
--                         special_instructions TEXT,
--                         is_paid BOOLEAN DEFAULT FALSE NOT NULL
-- );
--
-- -- Create order_items table
-- CREATE TABLE order_items (
--                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                              order_id BIGINT NOT NULL,
--                              menu_item_id BIGINT NOT NULL,
--                              menu_item_name VARCHAR(255) NOT NULL,
--                              menu_item_variant_id BIGINT,
--                              variant VARCHAR(100),
--                              size ENUM('SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE'),
--                              quantity INT NOT NULL,
--                              unit_price DECIMAL(19, 2) NOT NULL,
--                              sub_total DECIMAL(19, 2) NOT NULL,
--                              special_instructions TEXT,
--                              CONSTRAINT fk_order_items_order
--                                  FOREIGN KEY (order_id)
--                                      REFERENCES orders (id)
--                                      ON DELETE CASCADE
-- );
--
-- -- Create indexes for better performance
-- CREATE INDEX idx_orders_customer_id ON orders (customer_id);
-- CREATE INDEX idx_orders_order_status ON orders (order_status);
-- CREATE INDEX idx_orders_order_time ON orders (order_time);
-- CREATE INDEX idx_order_items_order_id ON order_items (order_id);