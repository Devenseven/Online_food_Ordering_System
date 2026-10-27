-- Seed categories
INSERT INTO categories (name, description, active) VALUES
('Starters', 'Appetizers and small bites', true),
('Main Course', 'Hearty main dishes', true),
('Pizza', 'Freshly baked pizzas', true),
('Burgers', 'Juicy burgers', true),
('Desserts', 'Sweet treats', true),
('Beverages', 'Refreshing drinks', true);

-- Starters
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Crispy Spring Rolls', 'Golden fried vegetable spring rolls', 4.99, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', true, true, (SELECT id FROM categories WHERE name='Starters')),
('Chicken Wings', 'Spicy buffalo chicken wings', 6.99, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=800', true, true, (SELECT id FROM categories WHERE name='Starters')),
('Garlic Bread', 'Toasted bread with garlic butter', 3.49, 'https://images.unsplash.com/photo-1573140401552-388e3c9af3f7?w=800', true, true, (SELECT id FROM categories WHERE name='Starters')),
('Mozzarella Sticks', 'Crispy fried mozzarella sticks', 5.49, 'https://images.unsplash.com/photo-1531749668029-2db88e4276c7?w=800', true, true, (SELECT id FROM categories WHERE name='Starters'));

-- Main Course
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Grilled Chicken Platter', 'Grilled chicken with rice and vegetables', 12.99, 'https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?w=800', true, true, (SELECT id FROM categories WHERE name='Main Course')),
('Beef Steak', 'Juicy sirloin steak cooked to perfection', 18.99, 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=800', true, true, (SELECT id FROM categories WHERE name='Main Course')),
('Pasta Alfredo', 'Creamy fettuccine alfredo', 11.49, 'https://images.unsplash.com/photo-1645112411341-6c4fd023714a?w=800', true, true, (SELECT id FROM categories WHERE name='Main Course')),
('Fish & Chips', 'Crispy battered fish with fries', 13.49, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=800', true, true, (SELECT id FROM categories WHERE name='Main Course'));

-- Pizza
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Margherita Pizza', 'Classic tomato, mozzarella and basil', 10.99, 'https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=800', true, true, (SELECT id FROM categories WHERE name='Pizza')),
('Pepperoni Pizza', 'Pepperoni with mozzarella cheese', 12.99, 'https://images.unsplash.com/photo-1628840042765-356cda07504e?w=800', true, true, (SELECT id FROM categories WHERE name='Pizza')),
('BBQ Chicken Pizza', 'BBQ chicken, onions and cheese', 13.99, 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800', true, true, (SELECT id FROM categories WHERE name='Pizza')),
('Vegetarian Pizza', 'Fresh vegetables and mozzarella', 11.99, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=800', true, true, (SELECT id FROM categories WHERE name='Pizza'));

-- Burgers
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Classic Cheeseburger', 'Beef patty with cheese, lettuce, tomato', 9.49, 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=800', true, true, (SELECT id FROM categories WHERE name='Burgers')),
('Bacon Burger', 'Beef patty with crispy bacon and cheese', 11.49, 'https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=800', true, true, (SELECT id FROM categories WHERE name='Burgers')),
('Chicken Burger', 'Crispy chicken with mayo and lettuce', 9.99, 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=800', true, true, (SELECT id FROM categories WHERE name='Burgers'));

-- Desserts
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Chocolate Cake', 'Rich chocolate cake slice', 6.49, 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800', true, true, (SELECT id FROM categories WHERE name='Desserts')),
('Ice Cream Sundae', 'Vanilla ice cream with chocolate sauce', 5.49, 'https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=800', true, true, (SELECT id FROM categories WHERE name='Desserts'));

-- Beverages
INSERT INTO food_items (name, description, price, image_url, available, active, category_id) VALUES
('Coca Cola', 'Chilled soft drink', 2.49, 'https://images.unsplash.com/photo-1554866585-cd94860890b7?w=800', true, true, (SELECT id FROM categories WHERE name='Beverages')),
('Fresh Orange Juice', 'Freshly squeezed orange juice', 4.49, 'https://images.unsplash.com/photo-1546554137-f86b9593a222?w=800', true, true, (SELECT id FROM categories WHERE name='Beverages'));
