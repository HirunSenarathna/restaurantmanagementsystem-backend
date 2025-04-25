-- Insert initial categories
INSERT INTO menu_categories (name, description) VALUES
                                                    ('Kottu', 'Traditional Sri Lankan stir-fried roti dish'),
                                                    ('Fried Rice', 'Various fried rice dishes'),
                                                    ('Rice and Curry', 'Traditional Sri Lankan rice with curry dishes'),
                                                    ('Short Eats', 'Snacks and quick bites'),
                                                    ('Beverages', 'Drinks and refreshments');

-- Insert sample menu items
INSERT INTO menu_items (name, description, category_id) VALUES
                                                            ('Vegetable Kottu', 'Vegetable kottu with mixed vegetables', 1),
                                                            ('Chicken Kottu', 'Chicken kottu with shredded chicken', 1),
                                                            ('Egg Fried Rice', 'Fried rice with egg', 2),
                                                            ('Chicken Fried Rice', 'Fried rice with chicken', 2),
                                                            ('Fish Curry Rice', 'Rice with fish curry and accompaniments', 3),
                                                            ('Chicken Kotthu Roll', 'Chicken kottu wrapped in a roti', 4),
                                                            ('Iced Coffee', 'Cold coffee with milk', 5);

-- Insert variants for menu items
INSERT INTO menu_item_variants (menu_item_id, size, variant, price, stock_quantity) VALUES
                                                                                        (1, 'SMALL', null, 300.00, 20),
                                                                                        (1, 'LARGE', null, 500.00, 15),
                                                                                        (2, 'SMALL', null, 350.00, 18),
                                                                                        (2, 'LARGE', null, 550.00, 12),
                                                                                        (3, 'SMALL', null, 250.00, 25),
                                                                                        (3, 'LARGE', null, 400.00, 20),
                                                                                        (4, 'SMALL', null, 300.00, 22),
                                                                                        (4, 'LARGE', null, 450.00, 18),
                                                                                        (5, null, 'Kiri Samba', 350.00, 30),
                                                                                        (5, null, 'Basmathi', 400.00, 25),
                                                                                        (6, null, null, 150.00, 40),
                                                                                        (7, 'SMALL', null, 200.00, 50),
                                                                                        (7, 'LARGE', null, 300.00, 40);