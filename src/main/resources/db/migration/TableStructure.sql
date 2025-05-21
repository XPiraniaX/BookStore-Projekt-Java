CREATE TABLE users (
                       id INT PRIMARY KEY,
                       username VARCHAR(255) UNIQUE,
                       password VARCHAR(255),
                       email VARCHAR(255),
                       role VARCHAR(50)
);

CREATE TABLE books (
                       id INT PRIMARY KEY,
                       title VARCHAR(255),
                       author VARCHAR(255),,
                       description VARCHAR(1000),
                       quantity INT,
                       available_quantity INT
);