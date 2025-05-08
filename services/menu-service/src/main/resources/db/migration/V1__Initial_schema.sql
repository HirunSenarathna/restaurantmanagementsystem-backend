-- V1__Initial_schema.sql
CREATE TABLE menu_categories (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL UNIQUE,
                                 description VARCHAR(255),
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE menu_items (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            category_id BIGINT NOT NULL,
                            available BOOLEAN DEFAULT TRUE,
                            image_url VARCHAR(255),
                            image_public_id VARCHAR(255),
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (category_id) REFERENCES menu_categories(id)
);

CREATE TABLE menu_item_variants (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    menu_item_id BIGINT NOT NULL,
                                    size ENUM('SMALL', 'LARGE'),
                                    variant VARCHAR(100),-- when fried rice they have variant like kiri samba ,basmathi rise
                                    price DECIMAL(10,2) NOT NULL,
                                    stock_quantity INT,
                                    available BOOLEAN DEFAULT TRUE,
                                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
                                    CONSTRAINT unique_variant UNIQUE (menu_item_id, size, variant)
);





-- -- Create menu_categories table
-- CREATE TABLE menu_categories (
--                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                                  name VARCHAR(100) NOT NULL UNIQUE,
--                                  description VARCHAR(255),
--                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );
--
-- -- Create menu_items table
-- CREATE TABLE menu_items (
--                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                             name VARCHAR(100) NOT NULL,
--                             description VARCHAR(255),
--                             category_id BIGINT NOT NULL,
--                             available BOOLEAN DEFAULT TRUE,
--                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                             FOREIGN KEY (category_id) REFERENCES menu_categories(id)
-- );
--
-- -- Create menu_item_variants table
-- CREATE TABLE menu_item_variants (
--                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                                     menu_item_id BIGINT NOT NULL,
--                                     size ENUM('SMALL', 'LARGE'),
--                                     variant VARCHAR(100),
--                                     price DECIMAL(10,2) NOT NULL,
--                                     stock_quantity INT,
--                                     available BOOLEAN DEFAULT TRUE,
--                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--                                     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--                                     FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
--                                     CONSTRAINT unique_variant UNIQUE (menu_item_id, size, variant)
-- );