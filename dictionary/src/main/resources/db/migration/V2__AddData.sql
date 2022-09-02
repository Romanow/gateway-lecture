-- V2 Add series and sets

INSERT INTO series(name, type, complexity, age)
VALUES ('Technic', 'TECHNIC', 'ADVANCED', 12),
       ('Creator Expert', 'SYSTEM', 'PROFESSIONAL', 18),
       ('Trains', 'SYSTEM', 'BEGINNER', 6),
       ('Castle', 'SYSTEM', 'BEGINNER', 6);

INSERT INTO lego_set(number, name, age, parts_count, suggested_price, series_id)
VALUES (42143, 'Ferrari Daytona SP3', 18, 3778, 399, 'Technic'),
       (42115, 'Lamborghini Sian FKP 37', 18, 3696, 379, 'Technic'),
       (42110, 'Land Rover Defender', 18, 2573, 199, 'Technic'),
       (42100, 'Liebherr R 9800 Excavator', 18, 4108, 449, 'Technic'),
       (42083, 'Bugatti Chiron', 11, 3599, 379, 'Technic'),
       (9398, '4x4 Crawler', 18, 1225, 199, 'Technic'),

       (10297, 'Boutique Hotel', 16, 3066, 199, 'Creator Expert'),
       (10264, 'Corner Garage', 16, 2569, 199, 'Creator Expert'),
       (10255, 'Assembly Square', 18, 4002, 279, 'Creator Expert'),
       (10270, 'Bookshop', 16, 2504, 199, 'Creator Expert'),

       (60336, 'Freight Train', 7, 1153, 199, 'Trains'),
       (60337, 'Express Passenger Train', 7, 764, 189, 'Trains'),
       (7898, 'Cargo Train Deluxe', 6, 856, 150, 'Trains'),
       (4559, 'Cargo Railway', 8, 835, 130, 'Trains'),
       (4565, 'Freight & Crane Railway', 8, 966, 140, 'Trains'),

       (70404, 'King''s Castle', 7, 996, 99, 'Castle'),
       (10193, 'Medieval Market Village', 12, 1601, 99, 'Castle'),
       (6090, 'Royal Knight', 8, 743, 95, 'Castle');