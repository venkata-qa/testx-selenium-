CREATE PROCEDURE testXDatabaseSetup()
BEGIN
    -- Create the database if it doesn't exist
    CREATE DATABASE IF NOT EXISTS testX;

    -- Create the 'reviews' table if it doesn't exist
    CREATE TABLE IF NOT EXISTS testX.reviews (
        id VARCHAR(50) NOT NULL,
        email VARCHAR(50) NOT NULL,
        date DATETIME DEFAULT NULL,
        price VARCHAR(50) DEFAULT NULL,
        id_number VARCHAR(50) DEFAULT NULL,
        description VARCHAR(50) DEFAULT NULL,
        PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

    -- Insert data into the 'reviews' table
    INSERT INTO testX.reviews (id, email, date, price, id_number, description)
    VALUES (1, 'example1user@email.com', '2023-08-15 07:44:08', '4.00', 1, 'First book is pretty good book overall');

    INSERT INTO testX.reviews (id, email, date, price, id_number, description)
    VALUES (2, 'example2user@email.com', '2023-08-15 07:44:08', '4.50', 2, 'Second book is pretty good book overall');
END;
